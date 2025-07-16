package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.model.Task;
import com.github.giga_chill.gigachill.service.*;
import com.github.giga_chill.gigachill.util.InfoEntityMapper;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.RequestTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskWithShoppingListsInfo;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("events/{eventId}/tasks")
@RequiredArgsConstructor
public class TasksController {

    private final Environment env;
    private final EventService eventService;
    private final UserService userService;
    private final ParticipantsService participantsService;
    private final TaskService taskService;
    private final ShoppingListsService shoppingListsService;
    private final ShoppingListsController shoppingListsController;

    @GetMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ResponseTaskInfo>> getTasks(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        return ResponseEntity.ok(
                taskService.getAllTasksFromEvent(eventId).stream()
                        .map(
                                item ->
                                        InfoEntityMapper.toResponseTaskInfo(
                                                item,
                                                taskService.taskPermissions(
                                                        eventId, item.getTaskId(), user.getId())))
                        .toList());
    }

    @PostMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> postTask(
            Authentication authentication,
            @PathVariable UUID eventId,
            @RequestBody RequestTaskInfo requestTaskInfo) {

        var user = userService.userAuthentication(authentication);
        var executorId =
                requestTaskInfo.executorId() != null
                        ? UuidUtils.safeUUID(requestTaskInfo.executorId())
                        : null;
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (executorId != null && !userService.userExistsById(executorId)) {
            throw new NotFoundException("User with id " + executorId + " not found");
        }
        if (executorId != null && !participantsService.isParticipant(eventId, executorId)) {
            throw new ForbiddenException(
                    "User with id "
                            + executorId
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (executorId != null && participantsService.isParticipantRole(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id " + user.getId() + " cannot assign executors");
        }

        taskService.createTask(eventId, user, requestTaskInfo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    // ACCESS: owner, admin, participant
    public ResponseEntity<ResponseTaskWithShoppingListsInfo> getTask(
            Authentication authentication, @PathVariable UUID eventId, @PathVariable UUID taskId) {
        var user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        return ResponseEntity.ok(
                toResponseTaskWithShoppingListsInfo(
                        eventId,
                        user.getId(),
                        taskService.getTaskById(taskId),
                        taskService.taskPermissions(eventId, taskId, user.getId())));
    }

    @PatchMapping("/{taskId}")
    // ACCESS: owner, admin, participant(Если является автором)
    public ResponseEntity<Void> patchTask(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID taskId,
            @RequestBody RequestTaskInfo requestTaskInfo) {
        var user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.completed"))) {
            throw new ConflictException("Task with id " + taskId + " is completed");
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !taskService.isAuthor(taskId, user.getId())) {
            throw new ForbiddenException(
                    "User with id " + user.getId() + " cannot change " + "task with id: " + taskId);
        }
        taskService.updateTask(eventId, taskId, requestTaskInfo);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}/executor")
    // ACCESS: owner, admin, participant(Если является автором)
    public ResponseEntity<Void> putTaskExecutor(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID taskId,
            @RequestBody Map<String, Object> body) {
        var user = userService.userAuthentication(authentication);
        if (!body.containsKey("executor_id")) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        UUID executorId =
                body.get("executor_id") != null
                        ? UuidUtils.safeUUID((String) body.get("executor_id"))
                        : null;
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (executorId != null && !userService.userExistsById(executorId)) {
            throw new NotFoundException("User with id " + executorId + " not found");
        }
        if (executorId != null && !participantsService.isParticipant(eventId, executorId)) {
            throw new ForbiddenException(
                    "User with id "
                            + executorId
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (executorId != null && participantsService.isParticipantRole(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " cannot assign executors "
                            + "to task with id: "
                            + taskId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !taskService.isAuthor(taskId, user.getId())) {
            throw new ForbiddenException(
                    "User with id " + user.getId() + " cannot change " + "task with id: " + taskId);
        }
        taskService.updateExecutor(taskId, executorId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}/shopping-lists")
    // ACCESS: owner, admin, participant(Если является автором)
    public ResponseEntity<Void> putTaskShoppingLists(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID taskId,
            @RequestBody List<String> body) {
        var user = userService.userAuthentication(authentication);
        var shoppingListsIds =
                body != null ? body.stream().map(UuidUtils::safeUUID).toList() : null;

        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.completed"))) {
            throw new ConflictException("Task with id " + taskId + " is completed");
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !taskService.isAuthor(taskId, user.getId())) {
            throw new ForbiddenException(
                    "User with id " + user.getId() + " cannot change " + "task with id: " + taskId);
        }
        if (shoppingListsIds != null && !shoppingListsService.areExisted(shoppingListsIds)) {
            throw new NotFoundException(
                    "One or more of the resources involved were not found: " + body);
        }
        if (shoppingListsIds != null
                && !shoppingListsService.canBindShoppingListsToTask(shoppingListsIds, taskId)) {
            throw new ConflictException(
                    "One or more lists are already linked to the task: " + body);
        }
        taskService.updateShoppingLists(taskId, shoppingListsIds);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    // ACCESS: owner, admin, participant(Если является автором)
    public ResponseEntity<Void> deleteTask(
            Authentication authentication, @PathVariable UUID eventId, @PathVariable UUID taskId) {
        var user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.completed"))) {
            throw new ConflictException("Task with id " + taskId + " is completed");
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !taskService.isAuthor(taskId, user.getId())) {
            throw new ForbiddenException(
                    "User with id " + user.getId() + " cannot delete " + "task with id: " + taskId);
        }

        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/take-in-work")
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> postExecutorToTask(
            Authentication authentication, @PathVariable UUID eventId, @PathVariable UUID taskId) {
        var user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (!taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.open"))) {
            throw new ConflictException("Task with id " + taskId + " is not open");
        }
        if (!taskService.canExecute(taskId, user.getId())) {
            throw new ConflictException(
                    "User with id "
                            + user.getId()
                            + " cannot execute "
                            + "task with id: "
                            + taskId);
        }

        taskService.startExecuting(taskId, user.getId());
        return ResponseEntity.noContent().build();
    }

    private ResponseTaskWithShoppingListsInfo toResponseTaskWithShoppingListsInfo(
            UUID eventId, UUID userI, Task task, Map<String, Boolean> permissions) {
        return new ResponseTaskWithShoppingListsInfo(
                task.getTaskId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadlineDatetime(),
                task.getActualApprovalId() != null ? task.getTaskId().toString() : null,
                permissions,
                InfoEntityMapper.toUserInfo(task.getAuthor()),
                task.getExecutor() != null ? InfoEntityMapper.toUserInfo(task.getExecutor()) : null,
                task.getShoppingLists().stream()
                        .map(
                                item ->
                                        InfoEntityMapper.toShoppingListInfo(
                                                item,
                                                shoppingListsController.canEdit(
                                                        eventId, item.getShoppingListId(), userI)))
                        .toList());
    }
}
