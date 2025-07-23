package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.TaskDAO;
import com.github.giga_chill.gigachill.data.transfer.object.TaskDTO;
import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.mapper.TaskMapper;
import com.github.giga_chill.gigachill.mapper.UserMapper;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.util.DtoEntityMapper;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.RequestTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskWithShoppingListsInfo;
import jakarta.annotation.Nullable;
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
    private final ShoppingListsService shoppingListsService;
    private final UserService userService;
    private final ParticipantsService participantsService;
    private final EventService eventService;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;


    public List<ResponseTaskInfo> getAllTasksFromEvent(UUID eventId, UUID userId) {
        return taskDAO.getAllTasksFromEvent(eventId).stream()
                .map(taskMapper::toResponseTaskInfo)
                .peek(item -> item.setPermissions(taskPermissions(eventId, UuidUtils.safeUUID(item.getTaskId()), userId)))
                .toList();
    }

    public ResponseTaskWithShoppingListsInfo getTaskById(UUID taskId, UUID eventId, UUID userId) {
        var task = taskMapper.toResponseTaskWithShoppingListsInfo(taskDAO.getTaskById(taskId));
        task.setPermissions(taskPermissions(eventId, taskId, userId));
        task.getShoppingLists()
                .forEach(
                        item ->
                                item.setStatus(
                                        shoppingListsService.getShoppingListStatus(
                                                UuidUtils.safeUUID(item.getShoppingListId()))));
        task.getShoppingLists()
                .forEach(item -> item.setCanEdit(shoppingListsService.canEdit(eventId, UuidUtils.safeUUID(item.getShoppingListId()), userId)));
        return task;
    }

    public String createTask(UUID eventId, User user, RequestTaskInfo requestTaskInfo) {
        var shoppingListsIds =
                requestTaskInfo.shoppingListsIds().stream().map(UuidUtils::safeUUID).toList();

        if (!shoppingListsService.areExisted(shoppingListsIds)) {
            throw new NotFoundException(
                    "One or more of the resources involved were not found: "
                            + requestTaskInfo.shoppingListsIds());
        }
        if (!shoppingListsService.canBindShoppingListsToTask(shoppingListsIds)) {
            throw new ConflictException(
                    "One or more lists are already linked to the task: "
                            + requestTaskInfo.shoppingListsIds());
        }

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
                        UUID.randomUUID(),
                        requestTaskInfo.title(),
                        requestTaskInfo.description(),
                        env.getProperty("task_status.open"),
                        requestTaskInfo.deadlineDatetime(),
                        null,
                        null,
                        userMapper.toDto(user),
                        requestTaskInfo.executorId() != null
                                ? userMapper.toDto(userService.getById(
                                        UuidUtils.safeUUID(requestTaskInfo.executorId())))
                                : null);

        taskDAO.createTask(eventId, task, shoppingListsIds);
        return task.getTaskId().toString();
    }

    public void updateTask(UUID eventId, UUID taskId, RequestTaskInfo requestTaskInfo) {
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

    public void startExecuting(UUID taskId, UUID userId) {
        taskDAO.startExecuting(taskId, userId);
    }

    public void deleteTask(UUID taskId) {
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

    public boolean canExecute(UUID taskId, UUID userId) {
        return taskDAO.canExecute(taskId, userId);
    }

    public UUID getExecutorId(UUID taskId) {
        return taskDAO.getExecutorId(taskId);
    }

    public void updateExecutor(UUID taskId, @Nullable UUID executorId) {
        taskDAO.updateExecutor(taskId, executorId);
    }

    public void updateShoppingLists(UUID taskId, List<UUID> shoppingLists) {
        taskDAO.updateShoppingLists(taskId, shoppingLists);
    }

    public void setExecutorComment(UUID taskId, String executorComment) {
        taskDAO.setExecutorComment(taskId, executorComment);
    }

    public void setReviewerComment(UUID taskId, String reviewerComment, boolean isApproved) {
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
                && (executorId == null || executorId.equals(userId))) {
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
