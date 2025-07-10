package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.model.ShoppingItem;
import com.github.giga_chill.gigachill.model.ShoppingList;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ShoppingListsService {

    private final Environment env;
    //TEMPORARY:
    private Map<UUID, Map<UUID, ShoppingList>> SHOPPING_LISTS = new HashMap<>();

    public List<ShoppingList> getAllShoppingListsFromEvent(UUID eventId) {
        //TODO: связь с бд

        //TEMPORARY:
        if (!SHOPPING_LISTS.containsKey(eventId)) {
            return List.of();
        }
        return SHOPPING_LISTS.get(eventId).values().stream().toList();
    }

    public ShoppingList getShoppingListById(UUID eventId, UUID shoppingListId) {
        //TODO: связь с бд (убрать eventId) (Из логгера тоже)

        //TEMPORARY:
        return SHOPPING_LISTS.get(eventId).get(shoppingListId);
    }

    public String createShoppingList(UUID eventId, UUID userId, String title, String description) {
        //TODO: связь с бд (убрать eventId)
        //TODO: сделать привязку к task_id
        ShoppingList shoppingList = new ShoppingList(UUID.randomUUID(), UUID.randomUUID(), title, description,
                env.getProperty("shopping_list_status.unassigned").toString(), new ArrayList<>(), new ArrayList<>());

        //TEMPORARY:
        SHOPPING_LISTS.computeIfAbsent(eventId, k -> new HashMap<>())
                .put(shoppingList.getShoppingListId(), shoppingList);
        return shoppingList.getShoppingListId().toString();
    }

    public void updateShoppingList(UUID eventId, UUID shoppingListId, String title, String description) {
        //TODO: связь с бд (убрать eventId)

        //TEMPORARY:
        ShoppingList shoppingList = SHOPPING_LISTS.get(eventId).get(shoppingListId);
        shoppingList.setTitle(title);
        shoppingList.setDescription(description);

    }


    public void deleteShoppingList(UUID eventId, UUID shoppingListId) {
        //TODO: связь с бд (убрать eventId)


        //TEMPORARY:
        SHOPPING_LISTS.get(eventId).remove(shoppingListId);
    }


    public String addShoppingItem(UUID eventId, UUID shoppingListId,
                                  String title, BigDecimal quantity, String unit) {
        //TODO: связь с бд (убрать eventId)
        ShoppingItem shoppingItem = new ShoppingItem(UUID.randomUUID(), title,
                quantity, unit, false);


        //TEMPORARY:
        SHOPPING_LISTS.get(eventId).get(shoppingListId).getShoppingItems().add(shoppingItem);
        return shoppingItem.getShoppingItemId().toString();
    }

    public void updateShoppingItem(UUID eventId, UUID shoppingListId, UUID shoppingItemId,
                                   String title, BigDecimal quantity, String unit) {
        //TODO: связь с бд (убрать eventId и shoppingListId)

        //TEMPORARY:
        ShoppingItem shoppingItem = SHOPPING_LISTS.get(eventId).get(shoppingListId).getShoppingItems().stream()
                .filter(item -> item.getShoppingItemId().equals(shoppingItemId))
                .findFirst().orElse(null);
        shoppingItem.setTitle(title);
        shoppingItem.setQuantity(quantity);
        shoppingItem.setUnit(unit);
    }


    public void deleteShoppingItemFromShoppingList(UUID eventId, UUID shoppingListId, UUID shoppingItemId) {
        //TODO: связь с бд (убрать eventId)


        //TEMPORARY:
        SHOPPING_LISTS.get(eventId).get(shoppingListId).getShoppingItems()
                .removeIf(item -> item.getShoppingItemId().equals(shoppingItemId));
    }

    public void updateShoppingItemStatus(UUID eventId, UUID shoppingListId, UUID shoppingItemId,
                                         boolean status) {
        //TODO: связь с бд (убрать eventId и shoppingListId)
        ShoppingItem shoppingItem = getShoppingItemById(eventId, shoppingListId, shoppingItemId);

        //TEMPORARY:
        shoppingItem.setIsPurchased(status);
    }

    public ShoppingItem getShoppingItemById(UUID eventId, UUID shoppingListId,
                                            UUID shoppingItemId) {
        //TODO: связь с бд (убрать eventId и shoppingListId)

        //TEMPORARY:
        return SHOPPING_LISTS.get(eventId).get(shoppingListId).getShoppingItems().stream()
                .filter(item -> item.getShoppingItemId().equals(shoppingItemId)).findFirst().orElse(null);
    }

    public void updateShoppingListConsumers(UUID eventId, UUID shoppingListId, List<UUID> allUserId) {
        //TODO: связь с бд (убрать eventId)


        //TEMPORARY:
        SHOPPING_LISTS.get(eventId).get(shoppingListId).getConsumers().clear();
    }

    public String getShoppingListStatus(UUID eventId, UUID shoppingListId) {
        //TODO: связь с бд (убрать eventId)

        //TEMPORARY:
        return SHOPPING_LISTS.get(eventId).get(shoppingListId).getStatus();
    }

    public boolean isExisted(UUID eventId, UUID shoppingListId) {
        //TODO: связь с бд (убрать eventId)

        //TEMPORARY:
        return SHOPPING_LISTS.get(eventId).containsKey(shoppingListId);
    }

    public boolean isConsumer(UUID eventId, UUID shoppingListId, UUID consumerId) {
        //TODO: связь с бд (убрать eventId)

        //TEMPORARY:
        return SHOPPING_LISTS.get(eventId).get(shoppingListId).getConsumers().stream()
                .anyMatch(item -> item.getId().equals(consumerId));
    }

    public boolean isShoppingItemExisted(UUID shoppingListId, UUID shoppingItemId) {
        //TODO: связь с бд (убрать shoppingListId)

        //TEMPORARY:
        return true;
    }
}
