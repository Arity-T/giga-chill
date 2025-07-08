package com.github.giga_chill.gigachill.service;

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

    public List<ShoppingList> getAllShoppingLists(String eventId){
        //TODO: связь с бд

        //TEMPORARY:
        if (!SHOPPING_LISTS.containsKey(eventId)){
            return List.of();
        }
        return SHOPPING_LISTS.get(eventId).values().stream().toList();
    }


    public ShoppingList createShoppingList(String eventId, String title, String description){
        //TODO: связь с бд
        ShoppingList shoppingList = new ShoppingList(UUID.randomUUID().toString(), "not yet", title, description,
                env.getProperty("shopping_list_status.unassigned").toString(), new ArrayList<>(), new ArrayList<>());

        //TEMPORARY:
        SHOPPING_LISTS.computeIfAbsent(eventId, k -> new HashMap<>())
                .put(shoppingList.getShoppingListId(), shoppingList);

        return shoppingList;
    }



}
