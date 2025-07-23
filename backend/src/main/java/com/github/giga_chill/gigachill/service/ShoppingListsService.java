package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.ShoppingListDAO;
import com.github.giga_chill.gigachill.data.access.object.TaskDAO;
import com.github.giga_chill.gigachill.data.transfer.object.ShoppingItemDTO;
import com.github.giga_chill.gigachill.mapper.ShoppingItemMapper;
import com.github.giga_chill.gigachill.mapper.ShoppingListMapper;
import com.github.giga_chill.gigachill.util.DtoEntityMapper;
import java.math.BigDecimal;
import java.util.*;

import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.ShoppingItemInfo;
import com.github.giga_chill.gigachill.web.info.ShoppingListInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingListsService {

    private final ShoppingListMapper shoppingListMapper;
    private final ShoppingItemMapper shoppingItemMapper;
    private final Environment env;
    private final ShoppingListDAO shoppingListDAO;
    private final TaskDAO taskDAO;
    private final ParticipantsService participantsService;

    public List<ShoppingListInfo> getAllShoppingListsFromEvent(UUID eventId, UUID userId) {
        return shoppingListDAO.getAllShoppingListsFromEvent(eventId).stream()
                .map(shoppingListMapper::toShoppingListInfo)
                .peek(item -> item.setStatus(getShoppingListStatus(UuidUtils.safeUUID(item.getShoppingListId()))))
                .peek(item -> item.setCanEdit(canEdit(eventId, UuidUtils.safeUUID(item.getShoppingListId()), userId)))
                .toList();
    }

    public ShoppingListInfo getShoppingListById(UUID shoppingListId) {
        var shoppingList =
                shoppingListMapper.toShoppingListInfo(
                        shoppingListDAO.getShoppingListById(shoppingListId));
        shoppingList.setStatus(getShoppingListStatus(shoppingListId));
        return shoppingList;
    }

    public List<ShoppingListInfo> getShoppingListsByIds(List<UUID> shoppingListsIds) {
        return shoppingListDAO.getShoppingListsByIds(shoppingListsIds).stream()
                .map(shoppingListMapper::toShoppingListInfo)
                .peek(item -> item.setStatus(getShoppingListStatus(UuidUtils.safeUUID(item.getShoppingListId()))))
                .toList();
    }

    public String createShoppingList(UUID eventId, UUID userId, String title, String description) {
        var shoppingListId = UUID.randomUUID();
        shoppingListDAO.createShoppingList(eventId, shoppingListId, userId, title, description);
        return shoppingListId.toString();
    }

    public void updateShoppingList(UUID shoppingListId, String title, String description) {
        shoppingListDAO.updateShoppingList(shoppingListId, title, description);
    }

    public void deleteShoppingList(UUID shoppingListId) {
        shoppingListDAO.deleteShoppingList(shoppingListId);
    }

    public String addShoppingItem(
            UUID shoppingListId, String title, BigDecimal quantity, String unit) {
        var shoppingItem = new ShoppingItemDTO(UUID.randomUUID(), title, quantity, unit, false);
        shoppingListDAO.addShoppingItem(
                shoppingListId, shoppingItem);
        return shoppingItem.getShoppingItemId().toString();
    }

    public void updateShoppingItem(
            UUID shoppingItemId, String title, BigDecimal quantity, String unit) {

        var shoppingItem = new ShoppingItemDTO(shoppingItemId, title, quantity, unit, null);
        shoppingListDAO.updateShoppingItem(shoppingItem);
    }

    public void deleteShoppingItemFromShoppingList(UUID shoppingListId, UUID shoppingItemId) {
        shoppingListDAO.deleteShoppingItemFromShoppingList(shoppingListId, shoppingItemId);
    }

    public void updateShoppingItemStatus(UUID shoppingItemId, boolean status) {
        shoppingListDAO.updateShoppingItemStatus(shoppingItemId, status);
    }

    public ShoppingItemInfo getShoppingItemById(UUID shoppingItemId) {
        return shoppingItemMapper.toShoppingItemInfo(
                shoppingListDAO.getShoppingItemById(shoppingItemId));
    }

    public void updateShoppingListConsumers(UUID shoppingListId, List<UUID> allUserId) {
        shoppingListDAO.updateShoppingListConsumers(shoppingListId, allUserId);
    }

    public UUID getTaskIdForShoppingList(UUID shoppingListId) {
        return shoppingListDAO.getTaskIdForShoppingList(shoppingListId);
    }

    public String getShoppingListStatus(UUID shoppingListId) {
        // TODO: Подумать про cancelled
        var taskId = getTaskIdForShoppingList(shoppingListId);
        if (taskId == null) {
            return env.getProperty("shopping_list_status.unassigned");
        }
        var taskStatus = taskDAO.getTaskStatus(taskId);
        if (taskStatus.equals(env.getProperty("task_status.open"))) {
            return env.getProperty("shopping_list_status.assigned");
        }
        if (taskStatus.equals(env.getProperty("task_status.in_progress"))
                || taskStatus.equals(env.getProperty("task_status.under_review"))) {
            return env.getProperty("shopping_list_status.in_progress");
        }
        if (taskStatus.equals(env.getProperty("task_status.completed"))) {
            if (shoppingListDAO.isBought(shoppingListId)) {
                return env.getProperty("shopping_list_status.bought");
            } else {
                return env.getProperty("shopping_list_status.partially_bought");
            }
        }
        throw new IllegalArgumentException("Invalid shopping list status");
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

    public boolean canBindShoppingListsToTask(List<UUID> shoppingListsIds, UUID taskId) {
        return shoppingListDAO.canBindShoppingListsToTaskById(shoppingListsIds, taskId);
    }

    public void setBudget(UUID shoppingItemId, BigDecimal budget) {
        shoppingListDAO.setBudget(shoppingItemId, budget);
    }

    public boolean canEdit(UUID eventId, UUID shoppingListId, UUID userId) {

        var isParticipant = participantsService.isParticipantRole(eventId, userId);
        var isConsumer =  isConsumer(shoppingListId, userId);
        if (isParticipant && !isConsumer) {
            return false;
        }

        var shoppingListStatus = getShoppingListStatus(shoppingListId);
        var isUnassigned =
                shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned"));
        var isAssigned =
                shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"));
        return (isUnassigned || isAssigned);
    }
}
