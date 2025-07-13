package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.TaskDAO;
import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.model.Task;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.util.DtoEntityMapper;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.RequestTaskInfo;
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

    public List<Task> getAllTasksFromEvent(UUID eventId) {
        return taskDAO.getAllTasksFromEvent(eventId).stream()
                .map(DtoEntityMapper::toTaskEntity)
                .toList();
    }

    public Task getTaskById(UUID taskId) {
        return DtoEntityMapper.toTaskEntity(taskDAO.getTaskById(taskId));
    }

    public String createTask(UUID eventId, User user, RequestTaskInfo requestTaskInfo) {
        List<UUID> shoppingListsIds =
                requestTaskInfo.shopping_lists_ids().stream().map(UuidUtils::safeUUID).toList();

        if (!shoppingListsService.areExisted(shoppingListsIds)) {
            throw new NotFoundException(
                    "One or more of the resources involved were not found: "
                            + requestTaskInfo.shopping_lists_ids());
        }
        if (!shoppingListsService.canBindShoppingListsToTask(shoppingListsIds)) {
            throw new ConflictException(
                    "One or more lists are already linked to the task: "
                            + requestTaskInfo.shopping_lists_ids());
        }

        Task task =
                new Task(
                        UUID.randomUUID(),
                        requestTaskInfo.title(),
                        requestTaskInfo.description(),
                        env.getProperty("task_status.open"),
                        requestTaskInfo.deadline_datetime(),
                        null,
                        user,
                        requestTaskInfo.executor_id() != null
                                ? userService.getById(
                                        UuidUtils.safeUUID(requestTaskInfo.executor_id()))
                                : null,
                        List.of());

        taskDAO.createTask(eventId, DtoEntityMapper.toTaskDto(task), shoppingListsIds);
        return task.getTaskId().toString();
    }

    public void updateTask(UUID taskId, RequestTaskInfo requestTaskInfo) {
        List<UUID> shoppingListsIds =
                requestTaskInfo.shopping_lists_ids() != null
                        ? requestTaskInfo.shopping_lists_ids().stream()
                                .map(UuidUtils::safeUUID)
                                .toList()
                        : null;

        if (shoppingListsIds != null && !shoppingListsService.areExisted(shoppingListsIds)) {
            throw new NotFoundException(
                    "One or more of the resources involved were not found: "
                            + requestTaskInfo.shopping_lists_ids());
        }
        if (shoppingListsIds != null
                && !shoppingListsService.canBindShoppingListsToTask(shoppingListsIds)) {
            throw new ConflictException(
                    "One or more lists are already linked to the task: "
                            + requestTaskInfo.shopping_lists_ids());
        }

        Task task =
                new Task(
                        taskId,
                        requestTaskInfo.title(),
                        requestTaskInfo.description(),
                        null,
                        requestTaskInfo.deadline_datetime(),
                        null,
                        null,
                        requestTaskInfo.executor_id() != null
                                ? userService.getById(
                                        UuidUtils.safeUUID(requestTaskInfo.executor_id()))
                                : null,
                        List.of());

        taskDAO.updateTask(taskId, DtoEntityMapper.toTaskDto(task), shoppingListsIds);
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
}
