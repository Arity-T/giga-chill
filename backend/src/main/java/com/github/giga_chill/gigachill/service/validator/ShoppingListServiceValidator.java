package com.github.giga_chill.gigachill.service.validator;

import com.github.giga_chill.gigachill.data.access.object.ShoppingListDAO;
import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShoppingListServiceValidator {

    private final ShoppingListDAO shoppingListDAO;
    private final ParticipantServiceValidator participantsServiceValidator;
    private final Environment env;

    public void checkIsExisted(UUID shoppingListId) {
        if (!shoppingListDAO.isExisted(shoppingListId)) {
            throw new NotFoundException("Shopping list with id: " + shoppingListId + " not found");
        }
    }

    public void checkUnassignedOrAssignedStatus(UUID shoppingListId, String shoppingListStatus) {
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned"))
                && !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " does not"
                            + " have unassigned or assigned status");
        }
    }

    public void checkShoppingItemIsExisted(UUID shoppingItemId) {
        if (!shoppingListDAO.isShoppingItemExisted(shoppingItemId)) {
            throw new NotFoundException("Shopping item with id: " + shoppingItemId + " not found");
        }
    }

    public void checkProgressOrBoughtOrPartiallyBoughtStatus(UUID shoppingListId, UUID executorId) {
        if (executorId == null) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " does not"
                            + " have \"in progress\", \"bought\" or \"partially_bought\" status");
        }
    }

    public void checkInProgressStatus(UUID shoppingListId, String shoppingListStatus) {
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.in_progress"))) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " does not"
                            + " have in progress status");
        }
    }

    public void checkOpportunityToChangeShoppingItemStatus(
            UUID eventId,
            UUID participantId,
            UUID shoppingListId,
            UUID executorId,
            String taskStatus) {
        if (executorId == null
                || !(executorId.equals(participantId)
                                && taskStatus.equals(env.getProperty("task_status.in_progress"))
                        || !(participantsServiceValidator.isParticipantRole(eventId, participantId)
                                && taskStatus.equals(env.getProperty("task_status.under_review"))
                                && executorId.equals(participantId)))) {
            throw new ForbiddenException(
                    "User with id: "
                            + participantId
                            + " can not change shopping item status in shopping list wih id: "
                            + shoppingListId);
        }
    }

    public void checkOpportunityToChangeBudget(
            UUID eventId, UUID shoppingListId, UUID userId, UUID executorId, String taskStatus) {
        if (!((taskStatus.equals(env.getProperty("task_status.in_progress"))
                        && executorId.equals(userId))
                || (taskStatus.equals(env.getProperty("task_status.under_review"))
                        && !participantsServiceValidator.isParticipantRole(eventId, userId)
                        && !executorId.equals(userId)))) {
            throw new ConflictException(
                    "User with id: "
                            + userId
                            + " cannot change budget of shopping list with id: "
                            + shoppingListId);
        }
    }

    public void checkConnectionWithTask(UUID shoppingListId, UUID taskId) {
        if (taskId == null) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " is not is not attached to the task");
        }
    }

    public void checkAreExisted(List<UUID> shoppingListsIds) {
        if (!shoppingListDAO.areExisted(shoppingListsIds)) {
            throw new NotFoundException(
                    "One or more of the resources involved were not found: " + shoppingListsIds);
        }
    }

    public void checkOpportunityToBindShoppingListsToTask(List<UUID> shoppingListsIds) {
        if (!shoppingListDAO.canBindShoppingListsToTask(shoppingListsIds)) {
            throw new ConflictException(
                    "One or more lists are already linked to the task: " + shoppingListsIds);
        }
    }
}
