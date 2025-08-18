package ru.gigachill.service;

import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.gigachill.data.access.object.ShoppingListDAO;
import ru.gigachill.data.access.object.TaskDAO;
import ru.gigachill.data.transfer.object.ShoppingItemDTO;
import ru.gigachill.exception.BadRequestException;
import ru.gigachill.mapper.ShoppingItemMapper;
import ru.gigachill.mapper.ShoppingListMapper;
import ru.gigachill.service.validator.*;
import ru.gigachill.web.api.model.*;

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

    public List<ShoppingListWithItems> getAllShoppingListsFromEvent(UUID eventId, UUID userId) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);

        return shoppingListDAO.getAllShoppingListsFromEvent(eventId).stream()
                .map(shoppingListMapper::toShoppingListWithItems)
                .peek(
                        item ->
                                item.setStatus(
                                        ShoppingListStatus.fromValue(
                                                getShoppingListStatus(item.getShoppingListId()))))
                .peek(item -> item.setCanEdit(canEdit(eventId, item.getShoppingListId(), userId)))
                .toList();
    }

    public String createShoppingList(
            UUID eventId, UUID userId, ShoppingListCreate shoppingListCreate) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);

        var shoppingListId = UUID.randomUUID();
        shoppingListDAO.createShoppingList(
                eventId,
                shoppingListId,
                userId,
                shoppingListCreate.getTitle(),
                shoppingListCreate.getDescription());
        return shoppingListId.toString();
    }

    public void updateShoppingList(
            UUID eventId, UUID userId, UUID shoppingListId, ShoppingListUpdate shoppingListUpdate) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);
        var shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);

        shoppingListDAO.updateShoppingList(
                shoppingListId, shoppingListUpdate.getTitle(), shoppingListUpdate.getDescription());
    }

    public void deleteShoppingList(UUID shoppingListId, UUID eventId, UUID userId) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);
        var shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);

        shoppingListDAO.deleteShoppingList(shoppingListId);
    }

    public String addShoppingItem(
            UUID shoppingListId, UUID eventId, UUID userId, ShoppingItemCreate shoppingItemCreate) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);

        var shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);

        var shoppingItem =
                new ShoppingItemDTO(
                        UUID.randomUUID(),
                        shoppingItemCreate.getTitle(),
                        shoppingItemCreate.getQuantity(),
                        shoppingItemCreate.getUnit(),
                        false);
        shoppingListDAO.addShoppingItem(shoppingListId, shoppingItem);
        return shoppingItem.getShoppingItemId().toString();
    }

    public void updateShoppingItem(
            UUID shoppingItemId,
            UUID eventId,
            UUID userId,
            UUID shoppingListId,
            ShoppingItemUpdate shoppingItemUpdate) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        shoppingListsServiceValidator.checkShoppingItemIsExisted(shoppingItemId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);
        var shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);

        var shoppingItem =
                new ShoppingItemDTO(
                        shoppingItemId,
                        shoppingItemUpdate.getTitle(),
                        shoppingItemUpdate.getQuantity(),
                        shoppingItemUpdate.getUnit(),
                        null);
        shoppingListDAO.updateShoppingItem(shoppingItem);
    }

    public void deleteShoppingItemFromShoppingList(
            UUID shoppingListId, UUID shoppingItemId, UUID eventId, UUID userId) {
        shoppingListsServiceValidator.checkShoppingItemIsExisted(shoppingItemId);
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);
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
            ShoppingItemSetPurchased shoppingItemSetPurchased) {

        var status = shoppingItemSetPurchased.getIsPurchased();

        shoppingListsServiceValidator.checkShoppingItemIsExisted(shoppingItemId);
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);
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

    public void updateShoppingListConsumers(
            UUID shoppingListId, UUID eventId, UUID userId, List<UUID> body) {
        if (body.isEmpty()) {
            throw new BadRequestException("Specify at least one consumer");
        }

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);
        participantsServiceValidator.checkIsConsumerOrAdminOrOwner(eventId, userId, shoppingListId);
        String shoppingListStatus = getShoppingListStatus(shoppingListId);
        shoppingListsServiceValidator.checkUnassignedOrAssignedStatus(
                shoppingListId, shoppingListStatus);

        userServiceValidator.checkAreExisted(body);

        shoppingListDAO.updateShoppingListConsumers(shoppingListId, body);
    }

    public UUID getTaskIdForShoppingList(UUID shoppingListId) {
        return shoppingListDAO.getTaskIdForShoppingList(shoppingListId);
    }

    public String getShoppingListStatus(UUID shoppingListId) {
        var taskId = getTaskIdForShoppingList(shoppingListId);
        if (Objects.isNull(taskId)) {
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
            UUID shoppingListId,
            UUID eventId,
            UUID userId,
            ShoppingListSetBudget shoppingListSetBudget) {

        var budget = shoppingListSetBudget.getBudget();
        if (budget.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Budget must be greater than 0");
        }

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        shoppingListsServiceValidator.checkIsExisted(shoppingListId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);
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
