package com.github.giga_chill.gigachill.data.access.object;

import com.github.giga_chill.gigachill.data.transfer.object.ShoppingItemDTO;
import com.github.giga_chill.gigachill.data.transfer.object.ShoppingListDTO;
import jakarta.annotation.Nullable;

import java.util.List;

/**
 * Data Access Object (DAO) interface for managing shopping lists and their items within events.
 * <p>
 * Provides CRUD operations and status checks for shopping lists and shopping items.
 * </p>
 */
public interface ShoppingListDAO {

    /**
     * Retrieves all shopping lists associated with the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a list of {@link ShoppingListDTO} objects for the event; empty list if none found
     */
    List<ShoppingListDTO> getAllShoppingListsFromEvent(String eventId);

    /**
     * Retrieves a specific shopping list by its identifier.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return the {@link ShoppingListDTO} matching the given ID
     */
    ShoppingListDTO getShoppingListById(String shoppingListId);

    /**
     * Creates a new shopping list with the given title and description.
     *
     * @param title       the title of the new shopping list
     * @param description the description of the new shopping list
     */
    void createShoppingList(String title, String description);

    /**
     * Updates the title and/or description of an existing shopping list.
     * Only non-null parameters will be applied.
     *
     * @param shoppingListId the unique identifier of the shopping list to update
     * @param title          the new title, or {@code null} to leave unchanged
     * @param description    the new description, or {@code null} to leave unchanged
     */
    void updateShoppingList(String shoppingListId, @Nullable String title, @Nullable String description);

    /**
     * Deletes the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list to delete
     */
    void deleteShoppingList(String shoppingListId);

    /**
     * Adds a new shopping item to the specified shopping list.
     *
     * @param shoppingListId    the unique identifier of the shopping list
     * @param shoppingItemDTO   the {@link ShoppingItemDTO} representing the new item
     */
    void addShoppingItem(String shoppingListId, ShoppingItemDTO shoppingItemDTO);

    /**
     * Removes an item from a shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param shoppingItemId the unique identifier of the item to remove
     */
    void deleteShoppingItemFromShoppingList(String shoppingListId, String shoppingItemId);

    /**
     * Updates the purchase status of a shopping item.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @param status         {@code true} if the item is purchased; {@code false} otherwise
     */
    void updateShoppingItemStatus(String shoppingItemId, boolean status);

    /**
     * Retrieves a shopping item by its identifier.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @return the {@link ShoppingItemDTO} matching the given ID
     */
    ShoppingItemDTO getShoppingItemById(String shoppingItemId);

    /**
     * Updates the list of consumer user IDs for a shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param allUserId      the list of user IDs who are allowed to consume this list
     */
    void updateShoppingListConsumers(String shoppingListId, List<String> allUserId);

    /**
     * Retrieves the current status of a shopping list (e.g., "open", "closed").
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return the status string of the shopping list
     */
    String getShoppingListStatus(String shoppingListId);

    /**
     * Checks whether a shopping list exists by its identifier.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @return {@code true} if the shopping list exists; {@code false} otherwise
     */
    boolean isExisted(String shoppingListId);

    /**
     * Checks whether a given user is a consumer of the specified shopping list.
     *
     * @param shoppingListId the unique identifier of the shopping list
     * @param consumerId     the unique identifier of the user
     * @return {@code true} if the user is a consumer; {@code false} otherwise
     */
    boolean isConsumer(String shoppingListId, String consumerId);

    /**
     * Checks whether a shopping item exists by its identifier.
     *
     * @param shoppingItemId the unique identifier of the shopping item
     * @return {@code true} if the shopping item exists; {@code false} otherwise
     */
    boolean isShoppingItemExisted(String shoppingItemId);

}
