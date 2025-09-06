package ru.gigachill.repository.composite.impl;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.gigachill.dto.ParticipantDTO;
import ru.gigachill.dto.ShoppingItemDTO;
import ru.gigachill.dto.ShoppingListDTO;
import ru.gigachill.jooq.generated.tables.records.ShoppingItemsRecord;
import ru.gigachill.jooq.generated.tables.records.ShoppingListsRecord;
import ru.gigachill.mapper.jooq.ParticipantsRecordMapper;
import ru.gigachill.mapper.jooq.ShoppingRecordsMapper;
import ru.gigachill.model.ConsumerWithUserData;
import ru.gigachill.model.ShoppingListWithDetails;
import ru.gigachill.repository.composite.ShoppingListCompositeRepository;
import ru.gigachill.repository.simple.*;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class ShoppingListCompositeRepositoryImpl implements ShoppingListCompositeRepository {
    private final ShoppingListRepository shoppingListRepository;
    private final ConsumerInListRepository consumerInListRepository;
    private final ShoppingItemRepository shoppingItemRepository;
    private final ShoppingRecordsMapper shoppingRecordsMapper;
    private final ParticipantsRecordMapper participantsRecordMapper;

    /**
     * Retrieves all shopping lists associated with the specified event.
     *
     * <p>This method performs efficient batch data aggregation: 1. Loads all shopping list details
     * and items in a single query 2. Loads all consumer assignments in a single query 3. Groups and
     * maps the data to avoid N+1 query problems
     */
    @Override
    public List<ShoppingListDTO> getAllShoppingListsFromEvent(UUID eventId) {
        List<ShoppingListWithDetails> rawData =
                shoppingListRepository.findByEventIdWithDetails(eventId);
        List<ConsumerWithUserData> allConsumers =
                consumerInListRepository.findAllConsumersForEventWithUserData(eventId);

        // Group consumers by shopping list ID for efficient lookup
        Map<UUID, List<ConsumerWithUserData>> consumersByList =
                allConsumers.stream()
                        .collect(Collectors.groupingBy(ConsumerWithUserData::getShoppingListId));

        return rawData.stream()
                .collect(Collectors.groupingBy(ShoppingListWithDetails::getShoppingListId))
                .values()
                .stream()
                .map(listData -> mapToShoppingListDTO(listData, consumersByList))
                .collect(Collectors.toList());
    }

    /**
     * Maps raw shopping list data to a complete ShoppingListDTO.
     *
     * <p>This method processes denormalized data from a JOIN query and: 1. Creates the base
     * shopping list DTO from the first record 2. Deduplicates shopping items (same item may appear
     * multiple times due to JOIN) 3. Attaches consumer participants from pre-loaded data
     *
     * @param listData denormalized shopping list data with items (from JOIN query)
     * @param consumersByList pre-grouped consumer data by shopping list ID
     * @return complete ShoppingListDTO with items and consumers
     */
    private ShoppingListDTO mapToShoppingListDTO(
            List<ShoppingListWithDetails> listData,
            Map<UUID, List<ConsumerWithUserData>> consumersByList) {
        ShoppingListWithDetails first = listData.getFirst();

        // Create base shopping list DTO
        ShoppingListDTO shoppingListDTO = shoppingRecordsMapper.toShoppingListDTO(first);

        // Extract unique items (deduplicate from JOIN result)
        Map<UUID, ShoppingItemDTO> uniqueItems = new LinkedHashMap<>();
        for (ShoppingListWithDetails data : listData) {
            if (data.getShoppingItemId() != null) {
                uniqueItems.putIfAbsent(
                        data.getShoppingItemId(), shoppingRecordsMapper.toShoppingItemDTO(data));
            }
        }

        // Attach consumers from pre-loaded data
        List<ParticipantDTO> consumers =
                consumersByList.getOrDefault(first.getShoppingListId(), List.of()).stream()
                        .map(participantsRecordMapper::toParticipantDTO)
                        .collect(Collectors.toList());

        shoppingListDTO.setShoppingItems(new ArrayList<>(uniqueItems.values()));
        shoppingListDTO.setConsumers(consumers);

        return shoppingListDTO;
    }

    @Override
    public ShoppingListDTO getShoppingListById(UUID shoppingListId) {
        Optional<ShoppingListsRecord> recordOpt = shoppingListRepository.findById(shoppingListId);

        if (recordOpt.isEmpty()) {
            return null;
        }

        ShoppingListsRecord record = recordOpt.get();
        List<ShoppingItemDTO> items =
                shoppingItemRepository.findByShoppingListId(shoppingListId).stream()
                        .map(shoppingRecordsMapper::toShoppingItemDTO)
                        .toList();

        List<ParticipantDTO> consumers =
                getConsumersForShoppingList(shoppingListId, record.getEventId());

        return shoppingRecordsMapper.toShoppingListDTOWithDetails(record, items, consumers);
    }

    @Transactional
    @Override
    public void createShoppingList(
            UUID eventId, UUID shoppingListId, UUID userId, String title, String description) {
        shoppingListRepository.save(
                new ShoppingListsRecord(
                        shoppingListId, null, eventId, title, description, null, null));

        consumerInListRepository.addConsumer(shoppingListId, userId);
    }

    @Transactional
    @Override
    public void updateShoppingList(
            UUID shoppingListId, @Nullable String title, @Nullable String description) {
        shoppingListRepository.updateShoppingList(shoppingListId, title, description);
    }

    @Transactional
    @Override
    public void deleteShoppingList(UUID shoppingListId) {
        shoppingListRepository.deleteById(shoppingListId);
    }

    @Transactional
    @Override
    public void addShoppingItem(UUID shoppingListId, ShoppingItemDTO shoppingItemDTO) {
        shoppingItemRepository.save(
                shoppingRecordsMapper.toShoppingItemsRecord(shoppingItemDTO, shoppingListId));
    }

    @Transactional
    @Override
    public void deleteShoppingItemFromShoppingList(UUID shoppingListId, UUID shoppingItemId) {
        shoppingItemRepository.deleteById(shoppingItemId);
    }

    @Transactional
    @Override
    public void updateShoppingItemStatus(UUID shoppingItemId, boolean status) {
        shoppingItemRepository.updateStatus(shoppingItemId, status);
    }

    @Override
    public ShoppingItemDTO getShoppingItemById(UUID shoppingItemId) {
        Optional<ShoppingItemsRecord> recordOpt = shoppingItemRepository.findById(shoppingItemId);
        return recordOpt.map(shoppingRecordsMapper::toShoppingItemDTO).orElse(null);
    }

    @Transactional
    @Override
    public void updateShoppingListConsumers(UUID shoppingListId, List<UUID> allUserIds) {
        List<UUID> currentUserIds = consumerInListRepository.findAllConsumers(shoppingListId);

        // Пользователи, которых нужно удалить (есть сейчас, но нет в новых)
        List<UUID> toRemove =
                currentUserIds.stream().filter(id -> !allUserIds.contains(id)).toList();

        // Пользователи, которых нужно добавить (есть в новых, но нет сейчас)
        List<UUID> toAdd = allUserIds.stream().filter(id -> !currentUserIds.contains(id)).toList();

        consumerInListRepository.deleteConsumers(shoppingListId, toRemove);
        consumerInListRepository.addConsumers(shoppingListId, toAdd);
    }

    @Override
    public boolean isExisted(UUID shoppingListId) {
        return shoppingListRepository.exists(shoppingListId);
    }

    @Override
    public boolean isConsumer(UUID shoppingListId, UUID consumerId) {
        return consumerInListRepository.isConsumer(shoppingListId, consumerId);
    }

    @Override
    public boolean isShoppingItemExisted(UUID shoppingItemId) {
        return shoppingItemRepository.exists(shoppingItemId);
    }

    @Transactional
    @Override
    public void updateShoppingItem(ShoppingItemDTO shoppingItemDTO) {
        shoppingItemRepository.update(
                shoppingItemDTO.getShoppingItemId(),
                shoppingItemDTO.getTitle(),
                shoppingItemDTO.getQuantity(),
                shoppingItemDTO.getUnit(),
                shoppingItemDTO.getIsPurchased());
    }

    @Override
    public List<ShoppingListDTO> getShoppingListsByIds(List<UUID> shoppingListsIds) {
        return shoppingListRepository.findByIds(shoppingListsIds).stream()
                .map(
                        list -> {
                            List<ShoppingItemDTO> items =
                                    shoppingItemRepository
                                            .findByShoppingListId(list.getShoppingListId())
                                            .stream()
                                            .map(shoppingRecordsMapper::toShoppingItemDTO)
                                            .toList();

                            List<ParticipantDTO> consumers =
                                    getConsumersForShoppingList(
                                            list.getShoppingListId(), list.getEventId());

                            return shoppingRecordsMapper.toShoppingListDTOWithDetails(
                                    list, items, consumers);
                        })
                .toList();
    }

    @Override
    public boolean areExisted(List<UUID> shoppingListsIds) {
        return shoppingListRepository.allExist(shoppingListsIds);
    }

    @Override
    public boolean canBindShoppingListToTask(UUID shoppingListId) {
        return shoppingListRepository.canBind(shoppingListId);
    }

    @Override
    public boolean canBindShoppingListsToTask(List<UUID> shoppingListsIds) {
        return shoppingListRepository.allCanBeBound(shoppingListsIds);
    }

    @Nullable
    @Override
    public UUID getTaskIdForShoppingList(UUID shoppingListId) {
        ShoppingListsRecord record = shoppingListRepository.findById(shoppingListId).orElse(null);
        if (record == null) {
            return null;
        }

        return record.getTaskId();
    }

    @Override
    public boolean isBought(UUID shoppingListId) {
        List<ShoppingItemsRecord> shoppingItems =
                shoppingItemRepository.findByShoppingListId(shoppingListId);

        for (ShoppingItemsRecord item : shoppingItems) {
            if (!item.getIsPurchased()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canBindShoppingListToTaskById(UUID shoppingListId, UUID taskId) {
        return shoppingListRepository.isBindedToTaskOrNull(shoppingListId, taskId);
    }

    @Override
    public boolean canBindShoppingListsToTaskById(List<UUID> shoppingListsIds, UUID taskId) {
        if (shoppingListsIds == null || shoppingListsIds.isEmpty()) return true;

        int count = shoppingListRepository.countAllBindedToThisTaskOrNull(shoppingListsIds, taskId);
        return count == shoppingListsIds.size();
    }

    @Transactional
    @Override
    public void setBudget(UUID shoppingListId, BigDecimal budget) {
        shoppingListRepository.setBudget(shoppingListId, budget);
    }

    @Override
    public List<ParticipantDTO> getConsumersForShoppingList(UUID shoppingListId, UUID eventId) {
        return consumerInListRepository
                .findAllConsumersWithUserData(shoppingListId, eventId)
                .stream()
                .map(participantsRecordMapper::toParticipantDTO)
                .toList();
    }

    @Override
    public UUID getReceiptIdByShoppingListId(UUID shoppingListId) {
        return shoppingListRepository.getReceiptIdByShoppingListId(shoppingListId);
    }

    @Override
    public void addReceiptIdByShoppingListId(UUID shoppingListId, UUID receiptId) {
        shoppingListRepository.addReceiptIdByShoppingListId(shoppingListId, receiptId);
    }

    @Override
    public void deleteReceiptIdByShoppingListId(UUID shoppingListId) {
        shoppingListRepository.setNullReceiptIdByShoppingListId(shoppingListId);
    }

    @Override
    public boolean canAddReceiptIdByShoppingListId(UUID shoppingListId) {
        return shoppingListRepository.getReceiptIdByShoppingListId(shoppingListId) == null;
    }
}
