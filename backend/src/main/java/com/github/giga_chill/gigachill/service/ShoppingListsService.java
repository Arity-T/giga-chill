package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.model.ShoppingItem;
import com.github.giga_chill.gigachill.model.ShoppingList;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ShoppingListsService {

    private final Environment env;
    //TEMPORARY:
    private Map<String, Map<String, ShoppingList>> SHOPPING_LISTS = new HashMap<>();

    public List<ShoppingList> getAllShoppingListsFromEvent(String eventId){
        //TODO: связь с бд

        //TEMPORARY:
        if (!SHOPPING_LISTS.containsKey(eventId)){
            return List.of();
        }
        return SHOPPING_LISTS.get(eventId).values().stream().toList();
    }

    public ShoppingList getShoppingListById(String eventId, String shoppingListId){
        //TODO: связь с бд (убрать eventId) (Из логгера тоже)

        //TEMPORARY:
        return SHOPPING_LISTS.get(eventId).get(shoppingListId);
    }

    public String createShoppingList(String eventId, String title, String description){
        //TODO: связь с бд (убрать eventId)
        ShoppingList shoppingList = new ShoppingList(UUID.randomUUID().toString(), "not yet", title, description,
                env.getProperty("shopping_list_status.unassigned").toString(), new ArrayList<>(), new ArrayList<>());

        //TEMPORARY:
        SHOPPING_LISTS.computeIfAbsent(eventId, k -> new HashMap<>())
                .put(shoppingList.getShoppingListId(), shoppingList);
        return shoppingList.getShoppingListId();
    }

    public void updateShoppingList(String eventId, String shoppingListId, String title, String description){
        //TODO: связь с бд (убрать eventId)

        //TEMPORARY:
        ShoppingList shoppingList = SHOPPING_LISTS.get(eventId).get(shoppingListId);
        shoppingList.setTitle(title);
        shoppingList.setDescription(description);

    }


    public void deleteShoppingList(String eventId, String shoppingListId){
        //TODO: связь с бд (убрать eventId)


        //TEMPORARY:
        SHOPPING_LISTS.get(eventId).remove(shoppingListId);
    }


    public String addShoppingItem(String eventId, String shoppingListId,
                                        String title, Integer quantity, String unit){
        //TODO: связь с бд (убрать eventId)
        ShoppingItem shoppingItem = new ShoppingItem(UUID.randomUUID().toString(), title,
                quantity, unit, false);


        //TEMPORARY:
        SHOPPING_LISTS.get(eventId).get(shoppingListId).getShoppingItems().add(shoppingItem);
        return shoppingItem.getShoppingItemId();
    }

    public void updateShoppingItem(String eventId, String shoppingListId, String shoppingItemId,
                                String title, Integer quantity, String unit){
        //TODO: связь с бд (убрать eventId и shoppingListId)

        //TEMPORARY:
        ShoppingItem shoppingItem = SHOPPING_LISTS.get(eventId).get(shoppingListId).getShoppingItems().stream()
                .filter(item-> item.getShoppingItemId().equals(shoppingItemId))
                .findFirst().orElse(null);
        shoppingItem.setTitle(title);
        shoppingItem.setQuantity(quantity);
        shoppingItem.setUnit(unit);
    }


    public void deleteShoppingItemFromShoppingList(String eventId, String shoppingListId, String shoppingItemId){
        //TODO: связь с бд (убрать eventId)


        //TEMPORARY:
        SHOPPING_LISTS.get(eventId).get(shoppingListId).getShoppingItems()
                .removeIf(item -> item.getShoppingItemId().equals(shoppingItemId));
    }

    public void updateShoppingItemStatus(String eventId, String shoppingListId, String shoppingItemId,
                                                 boolean status){
        //TODO: связь с бд (убрать eventId и shoppingListId)
        ShoppingItem shoppingItem = getShoppingItemById(eventId, shoppingListId, shoppingItemId);

        //TEMPORARY:
        shoppingItem.setIsPurchased(status);
    }

    public ShoppingItem getShoppingItemById(String eventId, String shoppingListId,
                                                            String shoppingItemId){
        //TODO: связь с бд (убрать eventId и shoppingListId)

        //TEMPORARY:
        return SHOPPING_LISTS.get(eventId).get(shoppingListId).getShoppingItems().stream()
                .filter(item-> item.getShoppingItemId().equals(shoppingItemId)).findFirst().orElse(null);
    }

    public void updateShoppingListConsumers(String eventId, String shoppingListId, List<String> allUserId){
        //TODO: связь с бд (убрать eventId)


        //TEMPORARY:
        SHOPPING_LISTS.get(eventId).get(shoppingListId).getConsumers().clear();
    }

    public String getShoppingListStatus(String eventId, String shoppingListId){
        //TODO: связь с бд (убрать eventId)

        //TEMPORARY:
        return SHOPPING_LISTS.get(eventId).get(shoppingListId).getStatus();
    }

    public boolean isExisted(String eventId, String shoppingListId){
        //TODO: связь с бд (убрать eventId)

        //TEMPORARY:
        return SHOPPING_LISTS.get(eventId).containsKey(shoppingListId);
    }

    public boolean isConsumer(String eventId, String shoppingListId, String consumerId){
        //TODO: связь с бд (убрать eventId)

        //TEMPORARY:
        return SHOPPING_LISTS.get(eventId).get(shoppingListId).getConsumers().stream()
                .anyMatch(item -> item.getId().equals(consumerId));
    }

    public boolean isShoppingItemExisted(String shoppingListId, String shoppingItemId) {
        //TODO: связь с бд (убрать shoppingListId)

        //TEMPORARY:
        return true;
    }
}
