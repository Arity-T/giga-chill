package ru.gigachill.repository.composite.impl;

import com.github.giga_chill.jooq.generated.tables.records.ShoppingItemsRecord;
import com.github.giga_chill.jooq.generated.tables.records.ShoppingListsRecord;
import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.gigachill.repository.composite.ShoppingListCompositeRepository;
import ru.gigachill.dto.ParticipantDTO;
import ru.gigachill.dto.ShoppingItemDTO;
import ru.gigachill.dto.ShoppingListDTO;
import ru.gigachill.repository.simple.*;
import ru.gigachill.mapper.jooq.ShoppingRecordsMapper;
import ru.gigachill.model.ShoppingListWithDetails;
import ru.gigachill.model.ConsumerWithUserData;
import ru.gigachill.mapper.ShoppingListWithDetailsMapper;
import ru.gigachill.mapper.ConsumerWithUserDataMapper;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class ShoppingListCompositeRepositoryImpl implements ShoppingListCompositeRepository {
    private final ShoppingListRepository shoppingListRepository;
    private final ConsumerInListRepository consumerInListRepository;
    private final ShoppingItemRepository shoppingItemRepository;
    private final ShoppingRecordsMapper shoppingRecordsMapper;
    private final ShoppingListWithDetailsMapper shoppingListWithDetailsMapper;
    private final ConsumerWithUserDataMapper consumerWithUserDataMapper;

    /**
     * Retrieves all shopping lists associated with the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a list of {@link ShoppingListDTO} objects for the event; empty list if none found
     */
    @Override
    public List<ShoppingListDTO> getAllShoppingListsFromEvent(UUID eventId) {
        List<ShoppingListWithDetails> rawData = shoppingListRepository.findByEventIdWithDetails(eventId);
        List<ConsumerWithUserData> allConsumers = consumerInListRepository.findAllConsumersForEventWithUserData(eventId);
        
        // Группируем потребителей по shoppingListId
        Map<UUID, List<ConsumerWithUserData>> consumersByList = allConsumers.stream()
                .collect(Collectors.groupingBy(ConsumerWithUserData::getShoppingListId));
        
        return rawData.stream()
                .collect(Collectors.groupingBy(ShoppingListWithDetails::getShoppingListId))
                .values()
                .stream()
                .map(listData -> mapToShoppingListDTO(listData, consumersByList))
                .collect(Collectors.toList());
    }
    
    private ShoppingListDTO mapToShoppingListDTO(List<ShoppingListWithDetails> listData, 
                                                Map<UUID, List<ConsumerWithUserData>> consumersByList) {
        ShoppingListWithDetails first = listData.getFirst();
        
        // Создаем основной объект списка покупок
        ShoppingListDTO shoppingListDTO = shoppingListWithDetailsMapper.toShoppingListDTO(first);
        
        // Извлекаем уникальные товары
        Map<UUID, ShoppingItemDTO> uniqueItems = new LinkedHashMap<>();
        for (ShoppingListWithDetails data : listData) {
            if (data.getShoppingItemId() != null) {
                uniqueItems.putIfAbsent(data.getShoppingItemId(), 
                    shoppingListWithDetailsMapper.toShoppingItemDTO(data));
            }
        }
        
        // Получаем потребителей из предварительно загруженных данных
        List<ParticipantDTO> consumers = consumersByList.getOrDefault(first.getShoppingListId(), List.of())
                .stream()
                .map(consumerWithUserDataMapper::toParticipantDTO)
                .collect(Collectors.toList());
        
        shoppingListDTO.setShoppingItems(new ArrayList<>(uniqueItems.values()));
        shoppingListDTO.setConsumers(consumers);
        
        return shoppingListDTO;
    }

    /**
     * Retrieves a specific shopping list by its identifier.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return the {@link ShoppingListDTO} matching the given ID
     */
    @Override
    public ShoppingListDTO getShoppingListById(UUID shoppingListId) {
        Optional<ShoppingListsRecord> recordOpt = shoppingListRepository.findById(shoppingListId);

        if (recordOpt.isEmpty()) {
            return null;
        }

        ShoppingListsRecord record = recordOpt.get();
        List<ShoppingItemDTO> items = shoppingItemRepository.findByShoppingListId(shoppingListId).stream()
                .map(shoppingRecordsMapper::toShoppingItemDTO)
                .toList();
        
        List<ParticipantDTO> consumers = getConsumersForShoppingList(shoppingListId, record.getEventId());
        
        return shoppingRecordsMapper.toShoppingListDTOWithDetails(record, items, consumers);
    }

    /**
     * Creates a new shopping list within the specified event.
     *
     * @param eventId the unique identifier of the event to which the shopping list belongs
     * @param shoppingListId the unique identifier to assign to the new shopping list
     * @param userId the unique identifier of the user creating the shopping list
     * @param title the title of the shopping list
     * @param description the description of the shopping list
     */
    @Transactional
    @Override
    public void createShoppingList(
            UUID eventId, UUID shoppingListId, UUID userId, String title, String description) {
        shoppingListRepository.save(
                new ShoppingListsRecord(
                        shoppingListId, null, eventId, title, description, null, null));

        consumerInListRepository.addConsumer(shoppingListId, userId);
    }

    /**
     * Updates the title and/or description of an existing shopping list. Only non-null parameters
     * will be applied.
     *
     * @param shoppingListId the unique identifier of the shopping list to update
     * @param title the new title, or {@code null} to leave unchanged
     * @param description the new description, or {@code null} to leave unchanged
     */
    @Transactional
    @Override
    public void updateShoppingList(
            UUID shoppingListId, @Nullable String title, @Nullable String description) {
        shoppingListRepository.updateShoppingList(shoppingListId, title, description);
    }

    /**
     * Deletes the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list to delete
     */
    @Transactional
    @Override
    public void deleteShoppingList(UUID shoppingListId) {
        shoppingListRepository.deleteById(shoppingListId);
    }

    /**
     * Adds a new shopping item to the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param shoppingItemDTO the {@link ShoppingItemDTO} representing the new item
     */
    @Transactional
    @Override
    public void addShoppingItem(UUID shoppingListId, ShoppingItemDTO shoppingItemDTO) {
        shoppingItemRepository.save(
                shoppingRecordsMapper.toShoppingItemsRecord(shoppingItemDTO, shoppingListId));
    }

    /**
     * Removes an item from a shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param shoppingItemId the unique identifier of the item to remove
     */
    @Transactional
    @Override
    public void deleteShoppingItemFromShoppingList(UUID shoppingListId, UUID shoppingItemId) {
        shoppingItemRepository.deleteById(shoppingItemId);
    }

    /**
     * Updates the purchase status of a shopping item.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @param status {@code true} if the item is purchased; {@code false} otherwise
     */
    @Transactional
    @Override
    public void updateShoppingItemStatus(UUID shoppingItemId, boolean status) {
        shoppingItemRepository.updateStatus(shoppingItemId, status);
    }

    /**
     * Retrieves a shopping item by its identifier.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @return the {@link ShoppingItemDTO} matching the given ID
     */
    @Override
    public ShoppingItemDTO getShoppingItemById(UUID shoppingItemId) {
        Optional<ShoppingItemsRecord> recordOpt = shoppingItemRepository.findById(shoppingItemId);
        return recordOpt.map(shoppingRecordsMapper::toShoppingItemDTO).orElse(null);
    }

    /**
     * Updates the list of consumer user IDs for a shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param allUserIds the list of user IDs who are allowed to consume this list
     */
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

    /**
     * Checks whether a shopping list exists by its identifier.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return {@code true} if the shopping list exists; {@code false} otherwise
     */
    @Override
    public boolean isExisted(UUID shoppingListId) {
        return shoppingListRepository.exists(shoppingListId);
    }

    /**
     * Checks whether a given user is a consumer of the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param consumerId the unique identifier of the user
     * @return {@code true} if the user is a consumer; {@code false} otherwise
     */
    @Override
    public boolean isConsumer(UUID shoppingListId, UUID consumerId) {
        return consumerInListRepository.isConsumer(shoppingListId, consumerId);
    }

    /**
     * Checks whether a shopping item exists by its identifier.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @return {@code true} if the shopping item exists; {@code false} otherwise
     */
    @Override
    public boolean isShoppingItemExisted(UUID shoppingItemId) {
        return shoppingItemRepository.exists(shoppingItemId);
    }

    /**
     * Updates the details of an existing shopping item.
     *
     * @param shoppingItemDTO the {@link ShoppingItemDTO} containing the new field values for the
     *     item
     */
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

    /**
     * Retrieves all shopping lists corresponding to the given identifiers.
     *
     * @param shoppingListsIds a {@link List} of {@link UUID} representing the IDs of the shopping
     *     lists to fetch
     * @return a {@link List} of {@link ShoppingListDTO} instances matching the provided IDs; if an
     *     ID does not correspond to an existing shopping list, it will be omitted
     */
    @Override
    public List<ShoppingListDTO> getShoppingListsByIds(List<UUID> shoppingListsIds) {
        return shoppingListRepository.findByIds(shoppingListsIds).stream()
                .map(list -> {
                    List<ShoppingItemDTO> items = shoppingItemRepository.findByShoppingListId(list.getShoppingListId()).stream()
                            .map(shoppingRecordsMapper::toShoppingItemDTO)
                            .toList();
                    
                    List<ParticipantDTO> consumers = getConsumersForShoppingList(list.getShoppingListId(), list.getEventId());
                    
                    return shoppingRecordsMapper.toShoppingListDTOWithDetails(list, items, consumers);
                })
                .toList();
    }

    /**
     * Verifies whether shopping lists with all specified identifiers exist.
     *
     * @param shoppingListsIds a {@link List} of {@link UUID} representing the IDs to check
     * @return {@code true} if a shopping list exists for every ID in the list; {@code false}
     *     otherwise
     */
    @Override
    public boolean areExisted(List<UUID> shoppingListsIds) {
        return shoppingListRepository.allExist(shoppingListsIds);
    }

    /**
     * Determines whether the specified shopping list is eligible to be bound to a task. The list is
     * considered free of a task if the taskId field is null.
     *
     * @param shoppingListId the unique identifier of the shopping list to check
     * @return {@code true} if the shopping list can be bound to a task; {@code false} otherwise
     */
    @Override
    public boolean canBindShoppingListToTask(UUID shoppingListId) {
        return shoppingListRepository.canBind(shoppingListId);
    }

    /**
     * Determines whether all the specified shopping lists are eligible to be bound to a task. The
     * list is considered free of a task if the taskId field is null.
     *
     * @param shoppingListsIds a {@link List} of {@link UUID} values representing the shopping lists
     *     to check
     * @return {@code true} if every shopping list in the list can be bound to a task; {@code false}
     *     if one or more cannot
     */
    @Override
    public boolean canBindShoppingListsToTask(List<UUID> shoppingListsIds) {
        return shoppingListRepository.allCanBeBound(shoppingListsIds);
    }

    /**
     * Retrieves the identifier of the task associated with the given shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return the {@link UUID} of the task linked to the specified shopping list. If the problem is
     *     not resolved, return null.
     */
    @Nullable
    @Override
    public UUID getTaskIdForShoppingList(UUID shoppingListId) {
        ShoppingListsRecord record = shoppingListRepository.findById(shoppingListId).orElse(null);
        if (record == null) {
            return null;
        }

        return record.getTaskId();
    }

    /**
     * Determines whether all products in this list are purchased (The is_purchased field of all
     * products is true).
     *
     * @param shoppingListId the unique identifier of the task
     * @return {@code true} if all is_purchased fields of the lists are true; {@code false}
     *     otherwise
     */
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

    /**
     * Determines whether a single shopping list can be bound to the given task.
     *
     * @param shoppingListId the unique identifier of the shopping list to check
     * @param taskId the unique identifier of the task
     * @return {@code true} if the shopping list is eligible to be associated with the task( If the
     *     shopping list is already linked to this task or is not linked to any task); {@code false}
     *     otherwise
     */
    @Override
    public boolean canBindShoppingListToTaskById(UUID shoppingListId, UUID taskId) {
        return shoppingListRepository.isBindedToTaskOrNull(shoppingListId, taskId);
    }

    /**
     * Determines whether all specified shopping lists can be bound to the given task.
     *
     * @param shoppingListsIds a {@link List} of {@link UUID} values representing shopping list IDs
     *     to check
     * @param taskId the unique identifier of the task
     * @return {@code true} if every shopping list in the list is eligible for association with the
     *     task( If the shopping list is already linked to this task or is not linked to any task);
     *     {@code false} otherwise
     */
    @Override
    public boolean canBindShoppingListsToTaskById(List<UUID> shoppingListsIds, UUID taskId) {
        if (shoppingListsIds == null || shoppingListsIds.isEmpty()) return true;

        int count = shoppingListRepository.countAllBindedToThisTaskOrNull(shoppingListsIds, taskId);
        return count == shoppingListsIds.size();
    }

    /**
     * Sets or updates the budget for the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param budget the {@link BigDecimal} amount representing the new budget
     */
    @Transactional
    @Override
    public void setBudget(UUID shoppingListId, BigDecimal budget) {
        shoppingListRepository.setBudget(shoppingListId, budget);
    }

    /**
     * Helper method to get consumers for a shopping list using the consumer mapper
     */
    private List<ParticipantDTO> getConsumersForShoppingList(UUID shoppingListId, UUID eventId) {
        return consumerInListRepository.findAllConsumersWithUserData(shoppingListId, eventId).stream()
                .map(consumerWithUserDataMapper::toParticipantDTO)
                .toList();
    }
}
