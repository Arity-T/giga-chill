package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.ShoppingListDAO;
import com.github.giga_chill.gigachill.data.access.object.TaskDAO;
import com.github.giga_chill.gigachill.data.transfer.object.ShoppingItemDTO;
import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.mapper.ShoppingItemMapper;
import com.github.giga_chill.gigachill.mapper.ShoppingListMapper;
import com.github.giga_chill.gigachill.service.validator.*;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.ShoppingItemInfo;
import com.github.giga_chill.gigachill.web.info.ShoppingListInfo;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingListService {

    private final ShoppingListMapper shoppingListMapper;
    private final ShoppingItemMapper shoppingItemMapper;
    private final Environment env;
    private final ShoppingListDAO shoppingListDAO;
    private final TaskDAO taskDAO;
    private final ParticipantService participantsService;
    private final ShoppingListServiceValidator shoppingListsServiceValidator;
    private final EventServiceValidator eventServiceValidator;
    private final ParticipantServiceValidator participantsServiceValidator;
    private final UserServiceValidator userServiceValidator;

    public List<ShoppingListInfo> getAllShoppingListsFromEvent(UUID eventId, UUID userId) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);

        return shoppingListDAO.getAllShoppingListsFromEvent(eventId).stream()
                .map(shoppingListMapper::toShoppingListInfo)
                .peek(
                        item ->
                                item.setStatus(
                                        getShoppingListStatus(
                                                UuidUtils.safeUUID(item.getShoppingListId()))))
                .peek(
                        item ->
                                item.setCanEdit(
                                        canEdit(
                                                eventId,
                                                UuidUtils.safeUUID(item.getShoppingListId()),
                                                userId)))
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
                .peek(
                        item ->
                                item.setStatus(
                                        getShoppingListStatus(
                                                UuidUtils.safeUUID(item.getShoppingListId()))))
                .toList();
    }

    public String createShoppingList(UUID eventId, UUID userId, Map<String, Object> body) {

        var title = (String) body.get("title");
        var description = (String) body.get("description");
        if (title == null || description == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);

        var shoppingListId = UUID.randomUUID();
        shoppingListDAO.createShoppingList(eventId, shoppingListId, userId, title, description);
        return shoppingListId.toString();
    }

    public void updateShoppingList(
            UUID eventId, UUID userId, UUID shoppingListId, Map<String, Object> body) {

        var title = (String) body.get("title");
        var description = (String) body.get("description");

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);
        var shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);

        shoppingListDAO.updateShoppingList(shoppingListId, title, description);
    }

    public void deleteShoppingList(UUID shoppingListId, UUID eventId, UUID userId) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);
        var shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);

        shoppingListDAO.deleteShoppingList(shoppingListId);
    }

    public String addShoppingItem(
            UUID shoppingListId, UUID eventId, UUID userId, Map<String, Object> body) {
        var title = (String) body.get("title");
        var quantity =
                body.get("quantity") != null
                        ? new BigDecimal(String.valueOf((Number) body.get("quantity")))
                        : null;
        var unit = (String) body.get("unit");
        if (title == null || quantity == null || unit == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);
        var shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);

        var shoppingItem = new ShoppingItemDTO(UUID.randomUUID(), title, quantity, unit, false);
        shoppingListDAO.addShoppingItem(shoppingListId, shoppingItem);
        return shoppingItem.getShoppingItemId().toString();
    }

    public void updateShoppingItem(
            UUID shoppingItemId,
            UUID eventId,
            UUID userId,
            UUID shoppingListId,
            Map<String, Object> body) {

        var title = (String) body.get("title");
        var quantity =
                body.get("quantity") != null
                        ? new BigDecimal(String.valueOf((Number) body.get("quantity")))
                        : null;
        var unit = (String) body.get("unit");

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        shoppingListsServiceValidator.checkShoppingItemIsExisted(shoppingItemId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);
        var shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);

        var shoppingItem = new ShoppingItemDTO(shoppingItemId, title, quantity, unit, null);
        shoppingListDAO.updateShoppingItem(shoppingItem);
    }

    public void deleteShoppingItemFromShoppingList(
            UUID shoppingListId, UUID shoppingItemId, UUID eventId, UUID userId) {
        shoppingListsServiceValidator.checkShoppingItemIsExisted(shoppingItemId);
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);
        String shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);

        shoppingListDAO.deleteShoppingItemFromShoppingList(shoppingListId, shoppingItemId);
    }

    public Boolean updateShoppingItemStatus(
            UUID shoppingItemId,
            UUID eventId,
            UUID userId,
            UUID shoppingListId,
            Map<String, Object> body) {

        var status = (Boolean) body.get("is_purchased");
        if (status == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }

        shoppingListsServiceValidator.checkShoppingItemIsExisted(shoppingItemId);
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        var shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkInProgressStatus(shoppingListId, shoppingListStatus);
        var taskId = getTaskIdForShoppingList(shoppingListId);
        shoppingListsServiceValidator.checkConnectionWithTask(shoppingListId, taskId);
        var executorId = taskDAO.getExecutorId(taskId);
        var taskStatus = taskDAO.getTaskStatus(taskId);
        shoppingListsServiceValidator.checkOpportunityToChangeShoppingItemStatus(
                eventId, userId, shoppingListId, executorId, taskStatus);

        shoppingListDAO.updateShoppingItemStatus(shoppingItemId, status);
        return status;
    }

    public ShoppingItemInfo getShoppingItemById(UUID shoppingItemId) {
        return shoppingItemMapper.toShoppingItemInfo(
                shoppingListDAO.getShoppingItemById(shoppingItemId));
    }

    public void updateShoppingListConsumers(
            UUID shoppingListId, UUID eventId, UUID userId, List<String> body) {
        if (body == null || body.isEmpty()) {
            throw new BadRequestException("Invalid request body: " + body);
        }

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);
        String shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);
        List<UUID> allUsersIds = body.stream().map(UuidUtils::safeUUID).toList();
        userServiceValidator.checkAreExisted(allUsersIds);

        shoppingListDAO.updateShoppingListConsumers(shoppingListId, allUsersIds);
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

    public boolean isConsumer(UUID shoppingListId, UUID consumerId) {
        return shoppingListDAO.isConsumer(shoppingListId, consumerId);
    }

    public BigDecimal setBudget(
            UUID shoppingListId, UUID eventId, UUID userId, Map<String, Object> body) {
        var budget =
                body.get("budget") != null
                        ? new BigDecimal(String.valueOf((Number) body.get("budget")))
                        : null;
        if (budget == null || budget.compareTo(new BigDecimal(0)) < 0) {
            throw new BadRequestException("Invalid request body: " + body);
        }

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        var taskId = getTaskIdForShoppingList(shoppingListId);
        var executorId = taskDAO.getExecutorId(taskId);
        shoppingListsServiceValidator.checkProgressOrBoughtOrPartiallyBoughtStatus(
                shoppingListId, executorId);
        var taskStatus = taskDAO.getTaskStatus(taskId);
        shoppingListsServiceValidator.checkOpportunityToChangeBudget(
                eventId, shoppingListId, userId, executorId, taskStatus);

        shoppingListDAO.setBudget(shoppingListId, budget);
        return budget;
    }

    public boolean canEdit(UUID eventId, UUID shoppingListId, UUID userId) {

        var isParticipant = participantsService.isParticipantRole(eventId, userId);
        var isConsumer = isConsumer(shoppingListId, userId);
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
