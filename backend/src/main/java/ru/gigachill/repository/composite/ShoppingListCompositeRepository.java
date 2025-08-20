package ru.gigachill.repository.composite;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import ru.gigachill.dto.ShoppingItemDTO;
import ru.gigachill.dto.ShoppingListDTO;

/**
 * Data Access Object (DAO) interface for managing shopping lists and their items within events.
 *
 * <p>Provides CRUD operations and status checks for shopping lists and shopping items.
 */
public interface ShoppingListCompositeRepository {

    /**
     * Retrieves all shopping lists associated with the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a list of {@link ShoppingListDTO} objects for the event; empty list if none found
     */
    List<ShoppingListDTO> getAllShoppingListsFromEvent(UUID eventId);

    /**
     * Retrieves a specific shopping list by its identifier.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return the {@link ShoppingListDTO} matching the given ID
     */
    ShoppingListDTO getShoppingListById(UUID shoppingListId);

    /**
     * Creates a new shopping list within the specified event.
     *
     * @param eventId the unique identifier of the event to which the shopping list belongs
     * @param shoppingListId the unique identifier to assign to the new shopping list
     * @param userId the unique identifier of the user creating the shopping list
     * @param title the title of the shopping list
     * @param description the description of the shopping list
     */
    void createShoppingList(
            UUID eventId, UUID shoppingListId, UUID userId, String title, String description);

    /**
     * Updates the title and/or description of an existing shopping list. Only non-null parameters
     * will be applied.
     *
     * @param shoppingListId the unique identifier of the shopping list to update
     * @param title the new title, or {@code null} to leave unchanged
     * @param description the new description, or {@code null} to leave unchanged
     */
    void updateShoppingList(
            UUID shoppingListId, @Nullable String title, @Nullable String description);

    /**
     * Deletes the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list to delete
     */
    void deleteShoppingList(UUID shoppingListId);

    /**
     * Adds a new shopping item to the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param shoppingItemDTO the {@link ShoppingItemDTO} representing the new item
     */
    void addShoppingItem(UUID shoppingListId, ShoppingItemDTO shoppingItemDTO);

    /**
     * Removes an item from a shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param shoppingItemId the unique identifier of the item to remove
     */
    void deleteShoppingItemFromShoppingList(UUID shoppingListId, UUID shoppingItemId);

    /**
     * Updates the purchase status of a shopping item.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @param status {@code true} if the item is purchased; {@code false} otherwise
     */
    void updateShoppingItemStatus(UUID shoppingItemId, boolean status);

    /**
     * Retrieves a shopping item by its identifier.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @return the {@link ShoppingItemDTO} matching the given ID
     */
    ShoppingItemDTO getShoppingItemById(UUID shoppingItemId);

    /**
     * Updates the list of consumer user IDs for a shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param allUserIds the list of user IDs who are allowed to consume this list
     */
    void updateShoppingListConsumers(UUID shoppingListId, List<UUID> allUserIds);

    /**
     * Checks whether a shopping list exists by its identifier.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return {@code true} if the shopping list exists; {@code false} otherwise
     */
    boolean isExisted(UUID shoppingListId);

    /**
     * Checks whether a given user is a consumer of the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param consumerId the unique identifier of the user
     * @return {@code true} if the user is a consumer; {@code false} otherwise
     */
    boolean isConsumer(UUID shoppingListId, UUID consumerId);

    /**
     * Checks whether a shopping item exists by its identifier.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @return {@code true} if the shopping item exists; {@code false} otherwise
     */
    boolean isShoppingItemExisted(UUID shoppingItemId);

    /**
     * Updates the details of an existing shopping item.
     *
     * @param shoppingItemDTO the {@link ShoppingItemDTO} containing the new field values for the
     *     item
     */
    void updateShoppingItem(ShoppingItemDTO shoppingItemDTO);

    /**
     * Retrieves all shopping lists corresponding to the given identifiers.
     *
     * @param shoppingListsIds a {@link List} of {@link UUID} representing the IDs of the shopping
     *     lists to fetch
     * @return a {@link List} of {@link ShoppingListDTO} instances matching the provided IDs; if an
     *     ID does not correspond to an existing shopping list, it will be omitted
     */
    List<ShoppingListDTO> getShoppingListsByIds(List<UUID> shoppingListsIds);

    /**
     * Verifies whether shopping lists with all specified identifiers exist.
     *
     * @param shoppingListsIds a {@link List} of {@link UUID} representing the IDs to check
     * @return {@code true} if a shopping list exists for every ID in the list; {@code false}
     *     otherwise
     */
    boolean areExisted(List<UUID> shoppingListsIds);

    /**
     * Determines whether the specified shopping list is eligible to be bound to a task. The list is
     * considered free of a task if the taskId field is null.
     *
     * @param shoppingListId the unique identifier of the shopping list to check
     * @return {@code true} if the shopping list can be bound to a task; {@code false} otherwise
     */
    boolean canBindShoppingListToTask(UUID shoppingListId);

    /**
     * Determines whether all of the specified shopping lists are eligible to be bound to a task.
     * The list is considered free of a task if the taskId field is null.
     *
     * @param shoppingListsIds a {@link List} of {@link UUID} values representing the shopping lists
     *     to check
     * @return {@code true} if every shopping list in the list can be bound to a task; {@code false}
     *     if one or more cannot
     */
    boolean canBindShoppingListsToTask(List<UUID> shoppingListsIds);

    /**
     * Retrieves the identifier of the task associated with the given shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return the {@link UUID} of the task linked to the specified shopping list. If the problem is
     *     not resolved, return null.
     */
    @Nullable
    UUID getTaskIdForShoppingList(UUID shoppingListId);

    /**
     * Determines whether all products in this list are purchased (The is_purchased field of all
     * products is true).
     *
     * @param shoppingListId the unique identifier of the task
     * @return {@code true} if all is_purchased fields of the lists are true; {@code false}
     *     otherwise
     */
    boolean isBought(UUID shoppingListId);

    /**
     * Determines whether a single shopping list can be bound to the given task.
     *
     * @param shoppingListId the unique identifier of the shopping list to check
     * @param taskId the unique identifier of the task
     * @return {@code true} if the shopping list is eligible to be associated with the task( If the
     *     shopping list is already linked to this task or is not linked to any task); {@code false}
     *     otherwise
     */
    boolean canBindShoppingListToTaskById(UUID shoppingListId, UUID taskId);

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
    boolean canBindShoppingListsToTaskById(List<UUID> shoppingListsIds, UUID taskId);

    /**
     * Sets or updates the budget for the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param budget the {@link BigDecimal} amount representing the new budget
     */
    void setBudget(UUID shoppingListId, BigDecimal budget);
}
