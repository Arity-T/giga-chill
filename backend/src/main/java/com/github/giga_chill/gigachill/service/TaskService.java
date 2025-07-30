package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.TaskDAO;
import com.github.giga_chill.gigachill.data.transfer.object.TaskDTO;
import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.mapper.TaskMapper;
import com.github.giga_chill.gigachill.mapper.UserMapper;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.validator.*;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.RequestTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskWithShoppingListsInfo;
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

    public List<ResponseTaskInfo> getAllTasksFromEvent(UUID eventId, UUID userId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);

        return taskDAO.getAllTasksFromEvent(eventId).stream()
                .map(taskMapper::toResponseTaskInfo)
                .peek(
                        item ->
                                item.setPermissions(
                                        taskPermissions(
                                                eventId,
                                                UuidUtils.safeUUID(item.getTaskId()),
                                                userId)))
                .toList();
    }

    public ResponseTaskWithShoppingListsInfo getTaskById(UUID taskId, UUID eventId, UUID userId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);

        var task = taskMapper.toResponseTaskWithShoppingListsInfo(taskDAO.getTaskById(taskId));
        task.setPermissions(taskPermissions(eventId, taskId, userId));
        task.getShoppingLists()
                .forEach(
                        item ->
                                item.setStatus(
                                        shoppingListsService.getShoppingListStatus(
                                                UuidUtils.safeUUID(item.getShoppingListId()))));
        task.getShoppingLists()
                .forEach(
                        item ->
                                item.setCanEdit(
                                        shoppingListsService.canEdit(
                                                eventId,
                                                UuidUtils.safeUUID(item.getShoppingListId()),
                                                userId)));
        return task;
    }

    public String createTask(UUID eventId, User user, RequestTaskInfo requestTaskInfo) {
        var executorId =
                !Objects.isNull(requestTaskInfo.executorId())
                        ? UuidUtils.safeUUID(requestTaskInfo.executorId())
                        : null;

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, user.getId());
        if (!Objects.isNull(executorId)) {
            userServiceValidator.checkIsExisted(executorId);
            participantsServiceValidator.checkIsParticipant(eventId, executorId);
        }
        var shoppingListsIds =
                requestTaskInfo.shoppingListsIds().stream().map(UuidUtils::safeUUID).toList();
        shoppingListsServiceValidator.checkAreExisted(shoppingListsIds);
        shoppingListsServiceValidator.checkOpportunityToBindShoppingListsToTask(shoppingListsIds);
        var eventEndDatetime = OffsetDateTime.parse(eventService.getEndDatetime(eventId));
        var taskDeadline = OffsetDateTime.parse(requestTaskInfo.deadlineDatetime());
        taskServiceValidator.checkTaskDeadline(eventEndDatetime, taskDeadline);

        var task =
                new TaskDTO(
                        UUID.randomUUID(),
                        requestTaskInfo.title(),
                        requestTaskInfo.description(),
                        env.getProperty("task_status.open"),
                        requestTaskInfo.deadlineDatetime(),
                        null,
                        null,
                        userMapper.toUserDto(user),
                        requestTaskInfo.executorId() != null
                                ? userMapper.toUserDto(
                                        userService.getById(
                                                UuidUtils.safeUUID(requestTaskInfo.executorId())))
                                : null);
        taskDAO.createTask(eventId, task, shoppingListsIds);
        return task.getTaskId().toString();
    }

    public void updateTask(
            UUID eventId, UUID taskId, UUID userId, RequestTaskInfo requestTaskInfo) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        taskServiceValidator.checkNotCompletedStatus(taskId, getTaskStatus(taskId));
        participantsServiceValidator.checkIsAuthorOrAdminOrOwner(eventId, userId, taskId);

        var eventEndDatetime = OffsetDateTime.parse(eventService.getEndDatetime(eventId));
        var taskDeadline = OffsetDateTime.parse(requestTaskInfo.deadlineDatetime());

        if (eventEndDatetime.isBefore(taskDeadline)) {
            throw new ConflictException(
                    "You cannot specify task due date: "
                            + taskDeadline
                            + " that is later than the end of the event: "
                            + eventEndDatetime);
        }
        var task =
                new TaskDTO(
                        taskId,
                        requestTaskInfo.title(),
                        requestTaskInfo.description(),
                        null,
                        requestTaskInfo.deadlineDatetime(),
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

    public UUID updateExecutor(UUID taskId, UUID eventId, UUID userId, Map<String, Object> body) {

        if (!body.containsKey("executor_id")) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        UUID executorId =
                !Objects.isNull(body.get("executor_id"))
                        ? UuidUtils.safeUUID((String) body.get("executor_id"))
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

    public void updateShoppingLists(UUID taskId, UUID eventId, UUID userId, List<String> body) {
        var shoppingListsIds =
                !Objects.isNull(body) ? body.stream().map(UuidUtils::safeUUID).toList() : null;

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
            UUID taskId, UUID eventId, UUID userId, Map<String, String> body) {

        var executorComment = body.get("executor_comment");
        if (Objects.isNull(executorComment)) {
            throw new BadRequestException("Invalid request body: " + body);
        }

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        taskServiceValidator.checkInProgressStatus(taskId, getTaskStatus(taskId));
        taskServiceValidator.checkOpportunityToSentTaskToReview(taskId, userId);

        taskDAO.setExecutorComment(taskId, executorComment);
        return executorComment;
    }

    public void setReviewerComment(
            UUID taskId, Map<String, Object> body, UUID eventId, UUID userId) {

        var reviewerComment = (String) body.get("reviewer_comment");
        var isApproved = (Boolean) body.get("is_approved");
        if (Objects.isNull(reviewerComment) || Objects.isNull(isApproved)) {
            throw new BadRequestException("Invalid request body: " + body);
        }

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        taskServiceValidator.checkIsExisted(eventId, taskId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        taskServiceValidator.checkUnderReviewStatus(taskId, getTaskStatus(taskId));
        taskServiceValidator.checkOpportunityToApproveTask(eventId, taskId, userId);

        taskDAO.setReviewerComment(taskId, reviewerComment, isApproved);
    }

    public Map<String, Boolean> taskPermissions(UUID eventId, UUID taskId, UUID userId) {
        Map<String, Boolean> permissions = new HashMap<>();
        var canEdit = true;
        var canTakeItToWork = false;
        var canReview = false;
        var executorId = getExecutorId(taskId);
        if (getTaskStatus(taskId).equals(env.getProperty("task_status.completed"))) {
            canEdit = false;
        }
        if (participantsService.isParticipantRole(eventId, userId) && !isAuthor(taskId, userId)) {
            canEdit = false;
        }
        if (getTaskStatus(taskId).equals(env.getProperty("task_status.open"))
                && (Objects.isNull(executorId) || executorId.equals(userId))) {
            canTakeItToWork = true;
        }
        if (getTaskStatus(taskId).equals(env.getProperty("task_status.under_review"))
                && !participantsService.isParticipantRole(eventId, userId)
                && !getExecutorId(taskId).equals(userId)) {
            canReview = true;
        }
        permissions.put("can_edit", canEdit);
        permissions.put("can_take_in_work", canTakeItToWork);
        permissions.put("can_review", canReview);
        return permissions;
    }
}
