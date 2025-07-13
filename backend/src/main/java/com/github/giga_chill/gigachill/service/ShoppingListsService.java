package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.ShoppingListDAO;
import com.github.giga_chill.gigachill.data.transfer.object.ShoppingItemDTO;
import com.github.giga_chill.gigachill.data.transfer.object.ShoppingListDTO;
import com.github.giga_chill.gigachill.model.ShoppingItem;
import com.github.giga_chill.gigachill.model.ShoppingList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ShoppingListsService {

    private final ShoppingListDAO shoppingListDAO;
    private final ParticipantsService participantsService;

    public List<ShoppingList> getAllShoppingListsFromEvent(UUID eventId) {
        return shoppingListDAO.getAllShoppingListsFromEvent(eventId).stream()
                .map(this::toEntity).toList();
    }

    public ShoppingList getShoppingListById(UUID shoppingListId) {
        return toEntity(shoppingListDAO.getShoppingListById(shoppingListId));
    }

    public List<ShoppingList> getShoppingListsByIds(List<UUID> shoppingListsIds){
        return shoppingListDAO.getShoppingListsByIds(shoppingListsIds).stream()
                .map(this::toEntity).toList();
    }

    public String createShoppingList(UUID eventId, UUID userId, String title, String description) {
        UUID shoppingListId = UUID.randomUUID();
        shoppingListDAO.createShoppingList(eventId, shoppingListId, userId, title, description);
        return shoppingListId.toString();
    }

    public void updateShoppingList(UUID shoppingListId, String title, String description) {
        shoppingListDAO.updateShoppingList(shoppingListId, title, description);
    }


    public void deleteShoppingList(UUID shoppingListId) {
        shoppingListDAO.deleteShoppingList(shoppingListId);
    }


    public String addShoppingItem(UUID shoppingListId, String title, BigDecimal quantity, String unit) {
        ShoppingItem shoppingItem = new ShoppingItem(UUID.randomUUID(), title,
                quantity, unit, false);
        shoppingListDAO.addShoppingItem(shoppingListId, shoppingItemToDTO(shoppingItem));
        return shoppingItem.getShoppingItemId().toString();
    }

    public void updateShoppingItem(UUID shoppingItemId, String title, BigDecimal quantity, String unit) {

        ShoppingItem shoppingItem = new ShoppingItem(shoppingItemId, title,
                quantity, unit, null);
        shoppingListDAO.updateShoppingItem(shoppingItemToDTO(shoppingItem));
    }

    public void deleteShoppingItemFromShoppingList(UUID shoppingListId, UUID shoppingItemId) {
        shoppingListDAO.deleteShoppingItemFromShoppingList(shoppingListId, shoppingItemId);
    }

    public void updateShoppingItemStatus(UUID shoppingItemId, boolean status) {
        shoppingListDAO.updateShoppingItemStatus(shoppingItemId, status);
    }

    public ShoppingItem getShoppingItemById(UUID shoppingItemId) {
        return shoppingItemToEntity(shoppingListDAO.getShoppingItemById(shoppingItemId));
    }

    public void updateShoppingListConsumers(UUID shoppingListId, List<UUID> allUserId) {
        shoppingListDAO.updateShoppingListConsumers(shoppingListId, allUserId);
    }

    public String getShoppingListStatus(UUID shoppingListId) {
        return shoppingListDAO.getShoppingListStatus(shoppingListId);
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

    public boolean canBindShoppingListsToTask(List<UUID> shoppingListsIds){
        return shoppingListDAO.canBindShoppingListsToTask(shoppingListsIds);
    }

    private ShoppingList toEntity(ShoppingListDTO shoppingListDTO) {
        return new ShoppingList(
                shoppingListDTO.shoppingListId(),
                shoppingListDTO.taskId(),
                shoppingListDTO.title(),
                shoppingListDTO.description(),
                shoppingListDTO.status(),
                shoppingListDTO.shoppingItems().stream()
                        .map(this::shoppingItemToEntity)
                        .toList(),
                shoppingListDTO.consumers().stream()
                        .map(participantsService::toEntity)
                        .toList()
        );
    }

    private ShoppingListDTO toDto(ShoppingList shoppingList) {
        return new ShoppingListDTO(
                shoppingList.getShoppingListId(),
                shoppingList.getTaskId(),
                shoppingList.getTitle(),
                shoppingList.getDescription(),
                shoppingList.getStatus(),
                shoppingList.getShoppingItems().stream()
                        .map(this::shoppingItemToDTO)
                        .toList(),
                shoppingList.getConsumers().stream()
                        .map(participantsService::toDto)
                        .toList()
        );
    }

    private ShoppingItemDTO shoppingItemToDTO(ShoppingItem shoppingItem){
        return new ShoppingItemDTO(
                shoppingItem.getShoppingItemId(),
                shoppingItem.getTitle(),
                shoppingItem.getQuantity(),
                shoppingItem.getUnit(),
                shoppingItem.getIsPurchased()
        );
    }

    private ShoppingItem shoppingItemToEntity(ShoppingItemDTO shoppingItemDTO){
        return new ShoppingItem(
                shoppingItemDTO.shoppingItemId(),
                shoppingItemDTO.title(),
                shoppingItemDTO.quantity(),
                shoppingItemDTO.unit(),
                shoppingItemDTO.isPurchased()
        );
    }
}
