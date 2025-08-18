package ru.gigachill.data.access.object.impl;

import com.github.giga_chill.jooq.generated.tables.records.ShoppingItemsRecord;
import com.github.giga_chill.jooq.generated.tables.records.ShoppingListsRecord;
import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gigachill.data.access.object.ShoppingListDAO;
import ru.gigachill.data.transfer.object.ParticipantDTO;
import ru.gigachill.data.transfer.object.ShoppingItemDTO;
import ru.gigachill.data.transfer.object.ShoppingListDTO;
import ru.gigachill.repository.*;

@Service
@RequiredArgsConstructor
public class ShoppingListDAOImpl implements ShoppingListDAO {
    private final ShoppingListRepository shoppingListRepository;
    private final ConsumerInListRepository consumerInListRepository;
    private final ShoppingItemRepository shoppingItemRepository;
    private final UserRepository userRepository;
    private final UserInEventRepository userInEventRepository;

    private List<ShoppingItemDTO> toShoppingItemDTO(UUID shoppingListId) {
        return shoppingItemRepository.findByShoppingListId(shoppingListId).stream()
                .map(
                        item ->
                                new ShoppingItemDTO(
                                        item.getShoppingItemId(),
                                        item.getTitle(),
                                        item.getQuantity(),
                                        item.getUnit(),
                                        item.getIsPurchased()))
                .toList();
    }

    private List<ParticipantDTO> toConsumerDTO(UUID shoppingListId) {
        Optional<ShoppingListsRecord> listOpt = shoppingListRepository.findById(shoppingListId);
        if (listOpt.isEmpty()) return List.of();

        UUID eventId = listOpt.get().getEventId();

        return consumerInListRepository.findAllConsumers(shoppingListId).stream()
                .map(
                        userId -> {
                            var userOpt = userRepository.findById(userId);
                            var userInEventOpt = userInEventRepository.findById(eventId, userId);

                            if (userOpt.isEmpty()) {
                                return new ParticipantDTO(userId, null, null, null, null);
                            }

                            var user = userOpt.get();
                            var userInEvent = userInEventOpt.orElse(null);

                            return new ParticipantDTO(
                                    user.getUserId(),
                                    user.getLogin(),
                                    user.getName(),
                                    userInEvent != null ? userInEvent.getRole().name() : null,
                                    userInEvent != null ? userInEvent.getBalance() : null);
                        })
                .toList();
    }

    /**
     * Retrieves all shopping lists associated with the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a list of {@link ShoppingListDTO} objects for the event; empty list if none found
     */
    @Override
    public List<ShoppingListDTO> getAllShoppingListsFromEvent(UUID eventId) {
        return shoppingListRepository.findByEventId(eventId).stream()
                .map(
                        list ->
                                new ShoppingListDTO(
                                        list.getShoppingListId(),
                                        list.getTaskId(),
                                        list.getTitle(),
                                        list.getDescription(),
                                        list.getBudget(),
                                        toShoppingItemDTO(list.getShoppingListId()),
                                        toConsumerDTO(list.getShoppingListId())))
                .toList();
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

        List<ShoppingItemDTO> shoppingItems = toShoppingItemDTO(record.getShoppingListId());
        List<ParticipantDTO> consumers = toConsumerDTO(shoppingListId);

        return new ShoppingListDTO(
                record.getShoppingListId(),
                record.getTaskId(),
                record.getTitle(),
                record.getDescription(),
                record.getBudget(),
                shoppingItems,
                consumers);
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
    @Override
    public void addShoppingItem(UUID shoppingListId, ShoppingItemDTO shoppingItemDTO) {
        shoppingItemRepository.save(
                new ShoppingItemsRecord(
                        shoppingItemDTO.getShoppingItemId(),
                        shoppingListId,
                        shoppingItemDTO.getTitle(),
                        shoppingItemDTO.getQuantity(),
                        shoppingItemDTO.getUnit(),
                        shoppingItemDTO.getIsPurchased()));
    }

    /**
     * Removes an item from a shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param shoppingItemId the unique identifier of the item to remove
     */
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

        if (recordOpt.isEmpty()) {
            return null;
        }

        ShoppingItemsRecord record = recordOpt.get();

        return new ShoppingItemDTO(
                record.getShoppingItemId(),
                record.getTitle(),
                record.getQuantity(),
                record.getUnit(),
                record.getIsPurchased());
    }

    /**
     * Updates the list of consumer user IDs for a shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param allUserIds the list of user IDs who are allowed to consume this list
     */
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
                .map(
                        list ->
                                new ShoppingListDTO(
                                        list.getShoppingListId(),
                                        list.getTaskId(),
                                        list.getTitle(),
                                        list.getDescription(),
                                        list.getBudget(),
                                        toShoppingItemDTO(list.getShoppingListId()),
                                        toConsumerDTO(list.getShoppingListId())))
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
    @Override
    public void setBudget(UUID shoppingListId, BigDecimal budget) {
        shoppingListRepository.setBudget(shoppingListId, budget);
    }
}
