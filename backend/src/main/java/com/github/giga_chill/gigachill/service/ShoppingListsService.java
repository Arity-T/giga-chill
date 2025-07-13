package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.ShoppingListDAO;
import com.github.giga_chill.gigachill.model.ShoppingItem;
import com.github.giga_chill.gigachill.model.ShoppingList;
import com.github.giga_chill.gigachill.util.DtoEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ShoppingListsService {

    private final ShoppingListDAO shoppingListDAO;

    public List<ShoppingList> getAllShoppingListsFromEvent(UUID eventId) {
        //TODO: сделать связь со статусом задачи
        return shoppingListDAO.getAllShoppingListsFromEvent(eventId).stream()
                .map(DtoEntityMapper::toShoppingListEntity).toList();
    }

    public ShoppingList getShoppingListById(UUID shoppingListId) {
        //TODO: сделать связь со статусом задачи
        return DtoEntityMapper.toShoppingListEntity(shoppingListDAO.getShoppingListById(shoppingListId));
    }

    public List<ShoppingList> getShoppingListsByIds(List<UUID> shoppingListsIds) {
        //TODO: сделать связь со статусом задачи
        return shoppingListDAO.getShoppingListsByIds(shoppingListsIds).stream()
                .map(DtoEntityMapper::toShoppingListEntity).toList();
    }

    public String createShoppingList(UUID eventId, UUID userId, String title, String description) {
        UUID shoppingListId = UUID.randomUUID();
        shoppingListDAO.createShoppingList(eventId, shoppingListId, userId, title, description);
        return shoppingListId.toString();
    }

    public void updateShoppingList(UUID shoppingListId, String title, String description) {
        //TODO: сделать связь со статусом задачи
        shoppingListDAO.updateShoppingList(shoppingListId, title, description);
    }


    public void deleteShoppingList(UUID shoppingListId) {
        shoppingListDAO.deleteShoppingList(shoppingListId);
    }


    public String addShoppingItem(UUID shoppingListId, String title, BigDecimal quantity, String unit) {
        ShoppingItem shoppingItem = new ShoppingItem(UUID.randomUUID(), title,
                quantity, unit, false);
        shoppingListDAO.addShoppingItem(shoppingListId, DtoEntityMapper.toShoppingItemDto(shoppingItem));
        return shoppingItem.getShoppingItemId().toString();
    }

    public void updateShoppingItem(UUID shoppingItemId, String title, BigDecimal quantity, String unit) {

        ShoppingItem shoppingItem = new ShoppingItem(shoppingItemId, title,
                quantity, unit, null);
        shoppingListDAO.updateShoppingItem(DtoEntityMapper.toShoppingItemDto(shoppingItem));
    }

    public void deleteShoppingItemFromShoppingList(UUID shoppingListId, UUID shoppingItemId) {
        shoppingListDAO.deleteShoppingItemFromShoppingList(shoppingListId, shoppingItemId);
    }

    public void updateShoppingItemStatus(UUID shoppingItemId, boolean status) {
        shoppingListDAO.updateShoppingItemStatus(shoppingItemId, status);
    }

    public ShoppingItem getShoppingItemById(UUID shoppingItemId) {
        return DtoEntityMapper.toShoppingItemEntity(shoppingListDAO.getShoppingItemById(shoppingItemId));
    }

    public void updateShoppingListConsumers(UUID shoppingListId, List<UUID> allUserId) {
        shoppingListDAO.updateShoppingListConsumers(shoppingListId, allUserId);
    }

    public String getShoppingListStatus(UUID shoppingListId) {
        //TODO: сделать связь со статусом задачи
        return "1";
    }

    public boolean isExisted(UUID shoppingListId) {
        return shoppingListDAO.isExisted(shoppingListId);
    }

    public boolean areExisted(List<UUID> shoppingListsIds) {
        return shoppingListDAO.areExisted(shoppingListsIds);
    }

    public boolean isConsumer(UUID shoppingListId, UUID consumerId) {
        return shoppingListDAO.isConsumer(shoppingListId, consumerId);
    }

    public boolean isShoppingItemExisted(UUID shoppingItemId) {
        return shoppingListDAO.isShoppingItemExisted(shoppingItemId);
    }

    public boolean canBindShoppingListsToTask(List<UUID> shoppingListsIds) {
        return shoppingListDAO.canBindShoppingListsToTask(shoppingListsIds);
    }

}
