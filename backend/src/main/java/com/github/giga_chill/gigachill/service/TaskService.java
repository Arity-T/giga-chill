package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.TaskDAO;
import com.github.giga_chill.gigachill.data.transfer.object.TaskDTO;
import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.mapper.TaskMapper;
import com.github.giga_chill.gigachill.mapper.UserMapper;
import com.github.giga_chill.gigachill.model.UserEntity;
import com.github.giga_chill.gigachill.service.validator.*;
import com.github.giga_chill.gigachill.web.api.model.*;
import java.time.OffsetDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final Environment env;
    private final TaskDAO taskDAO;
    private final ShoppingListService shoppingListsService;
    private final UserService userService;
    private final ParticipantService participantsService;
    private final EventService eventService;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;
    private final EventServiceValidator eventServiceValidator;
    private final ParticipantServiceValidator participantsServiceValidator;
    private final ShoppingListServiceValidator shoppingListsServiceValidator;
    private final TaskServiceValidator taskServiceValidator;
    private final UserServiceValidator userServiceValidator;

    public List<Task> getAllTasksFromEvent(UUID eventId, UUID userId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);

        return taskDAO.getAllTasksFromEvent(eventId).stream()
                .map(taskMapper::toTask)
                .peek(
                        item ->
                                item.setPermissions(
                                        taskPermissions(eventId, item.getTaskId(), userId)))
                .toList();
    }

    public TaskWithShoppingLists getTaskById(UUID taskId, UUID eventId, UUID userId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);

        var task = taskMapper.toTaskWithShoppingLists(taskDAO.getTaskById(taskId));
        task.setPermissions(taskPermissions(eventId, taskId, userId));
        task.getShoppingLists()
                .forEach(
                        item ->
                                item.setStatus(
                                        ShoppingListStatus.fromValue(
                                                shoppingListsService.getShoppingListStatus(
                                                        item.getShoppingListId()))));
        task.getShoppingLists()
                .forEach(
                        item ->
                                item.setCanEdit(
                                        shoppingListsService.canEdit(
                                                eventId, item.getShoppingListId(), userId)));
        return task;
    }

    public String createTask(UUID eventId, UserEntity userEntity, TaskCreate taskCreate) {
        var executorId =
                !Objects.isNull(taskCreate.getExecutorId()) ? taskCreate.getExecutorId() : null;

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userEntity.getId());
        if (!Objects.isNull(executorId)) {
            userServiceValidator.checkIsExisted(executorId);
            participantsServiceValidator.checkIsParticipant(eventId, executorId);
        }
        var shoppingListsIds = taskCreate.getShoppingListsIds();
        shoppingListsServiceValidator.checkAreExisted(shoppingListsIds);
        shoppingListsServiceValidator.checkOpportunityToBindShoppingListsToTask(shoppingListsIds);
        var eventEndDatetime = OffsetDateTime.parse(eventService.getEndDatetime(eventId));
        taskServiceValidator.checkTaskDeadline(eventEndDatetime, taskCreate.getDeadlineDatetime());

        var task =
                new TaskDTO(
                        UUID.randomUUID(),
                        taskCreate.getTitle(),
                        taskCreate.getDescription(),
                        env.getProperty("task_status.open"),
                        taskCreate.getDeadlineDatetime(),
                        null,
                        null,
                        userMapper.toUserDto(userEntity),
                        executorId != null
                                ? userMapper.toUserDto(userService.getById(executorId))
                                : null);
        taskDAO.createTask(eventId, task, shoppingListsIds);
        return task.getTaskId().toString();
    }

    public void updateTask(UUID eventId, UUID taskId, UUID userId, TaskUpdate taskUpdate) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        taskServiceValidator.checkNotCompletedStatus(taskId, getTaskStatus(taskId));
        participantsServiceValidator.checkIsAuthorOrAdminOrOwner(eventId, userId, taskId);
        var eventEndDatetime = OffsetDateTime.parse(eventService.getEndDatetime(eventId));
        taskServiceValidator.checkTaskDeadline(eventEndDatetime, taskUpdate.getDeadlineDatetime());

        var task =
                new TaskDTO(
                        taskId,
                        taskUpdate.getTitle(),
                        taskUpdate.getDescription(),
                        null,
                        taskUpdate.getDeadlineDatetime(),
                        null,
                        null,
                        null,
                        null);
        taskDAO.updateTask(taskId, task);
    }

    public void startExecuting(UUID taskId, UUID userId, UUID eventId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        taskServiceValidator.checkNotCompletedStatus(taskId, getTaskStatus(taskId));
        taskServiceValidator.checkExecutionOpportunity(taskId, userId);

        taskDAO.startExecuting(taskId, userId);
    }

    public void deleteTask(UUID taskId, UUID eventId, UUID userId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        taskServiceValidator.checkNotCompletedStatus(taskId, getTaskStatus(taskId));
        participantsServiceValidator.checkIsAuthorOrAdminOrOwner(eventId, userId, taskId);

        taskDAO.deleteTask(taskId);
    }

    public boolean isAuthor(UUID taskId, UUID userId) {
        return taskDAO.isAuthor(taskId, userId);
    }

    public String getTaskStatus(UUID taskId) {
        return taskDAO.getTaskStatus(taskId);
    }

    public boolean isExisted(UUID eventId, UUID taskId) {
        return taskDAO.isExisted(eventId, taskId);
    }

    public UUID getExecutorId(UUID taskId) {
        return taskDAO.getExecutorId(taskId);
    }

    public UUID updateExecutor(
            UUID taskId, UUID eventId, UUID userId, TaskSetExecutor taskSetExecutor) {

        UUID executorId =
                !Objects.isNull(taskSetExecutor.getExecutorId())
                        ? taskSetExecutor.getExecutorId().get()
                        : null;

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkIsAuthorOrAdminOrOwner(eventId, userId, taskId);
        if (!Objects.isNull(executorId)) {
            userServiceValidator.checkIsExisted(executorId);
            participantsServiceValidator.checkIsParticipant(eventId, executorId);
        }

        taskDAO.updateExecutor(taskId, executorId);
        return executorId;
    }

    public void updateShoppingLists(UUID taskId, UUID eventId, UUID userId, List<UUID> body) {
        var shoppingListsIds = !Objects.isNull(body) ? body : null;

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        taskServiceValidator.checkNotCompletedStatus(taskId, getTaskStatus(taskId));
        participantsServiceValidator.checkIsAuthorOrAdminOrOwner(eventId, userId, taskId);
        if (!Objects.isNull(shoppingListsIds)) {
            shoppingListsServiceValidator.checkAreExisted(shoppingListsIds);
            shoppingListsServiceValidator.checkOpportunityToBindShoppingListsToTask(
                    shoppingListsIds);
        }

        taskDAO.updateShoppingLists(taskId, shoppingListsIds);
    }

    public String setExecutorComment(
            UUID taskId,
            UUID eventId,
            UUID userId,
            TaskSendForReviewRequest taskSendForReviewRequest) {

        if (Objects.isNull(taskSendForReviewRequest.getExecutorComment())) {
            throw new BadRequestException(
                    "Invalid request body: " + taskSendForReviewRequest.toString());
        }

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        taskServiceValidator.checkInProgressStatus(taskId, getTaskStatus(taskId));
        taskServiceValidator.checkOpportunityToSentTaskToReview(taskId, userId);

        taskDAO.setExecutorComment(taskId, taskSendForReviewRequest.getExecutorComment());
        return taskSendForReviewRequest.getExecutorComment();
    }

    public void setReviewerComment(
            UUID taskId, TaskReviewRequest taskReviewRequest, UUID eventId, UUID userId) {

        if (Objects.isNull(taskReviewRequest.getReviewerComment())
                || Objects.isNull(taskReviewRequest.getIsApproved())) {
            throw new BadRequestException("Invalid request body: " + taskReviewRequest.toString());
        }

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        taskServiceValidator.checkUnderReviewStatus(taskId, getTaskStatus(taskId));
        taskServiceValidator.checkOpportunityToApproveTask(eventId, taskId, userId);

        taskDAO.setReviewerComment(
                taskId, taskReviewRequest.getReviewerComment(), taskReviewRequest.getIsApproved());
    }

    public Permissions taskPermissions(UUID eventId, UUID taskId, UUID userId) {
        var permissions = new Permissions();
        permissions.setCanEdit(true);
        permissions.setCanTakeInWork(false);
        permissions.setCanReview(false);
        var executorId = getExecutorId(taskId);
        if (getTaskStatus(taskId).equals(env.getProperty("task_status.completed"))) {
            permissions.setCanEdit(false);
        }
        if (participantsService.isParticipantRole(eventId, userId) && !isAuthor(taskId, userId)) {
            permissions.setCanEdit(false);
        }
        if (getTaskStatus(taskId).equals(env.getProperty("task_status.open"))
                && (Objects.isNull(executorId) || executorId.equals(userId))) {
            permissions.canTakeInWork(true);
        }
        if (getTaskStatus(taskId).equals(env.getProperty("task_status.under_review"))
                && !participantsService.isParticipantRole(eventId, userId)
                && !getExecutorId(taskId).equals(userId)) {
            permissions.setCanReview(true);
        }
        return permissions;
    }
}
