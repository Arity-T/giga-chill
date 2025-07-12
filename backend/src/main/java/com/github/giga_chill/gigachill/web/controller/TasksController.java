package com.github.giga_chill.gigachill.web.controller;


import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.model.Task;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.*;
import com.github.giga_chill.gigachill.util.InfoEntityMapper;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.RequestTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskWithShoppingListsInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("events/{eventId}/tasks")
@RequiredArgsConstructor
public class TasksController {


    private final Environment env;
    private final EventService eventService;
    private final UserService userService;
    private final ParticipantsService participantsService;
    private final TaskService taskService;
    private final ShoppingListsController shoppingListsController;

    @GetMapping
    public ResponseEntity<List<ResponseTaskInfo>> getTasks(Authentication authentication,
                                                           @PathVariable UUID eventId) {
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }


        return ResponseEntity.ok(taskService.getAllTasksFromEvent(eventId).stream()
                .map(InfoEntityMapper::toResponseTaskInfo).toList());
    }

    @PostMapping
    public ResponseEntity<Void> postTask(Authentication authentication,
                                         @PathVariable UUID eventId,
                                         @RequestBody RequestTaskInfo requestTaskInfo) {

        User user = userService.userAuthentication(authentication);
        UUID executorId = requestTaskInfo.executor_id() != null ?
                UuidUtils.safeUUID(requestTaskInfo.executor_id()) : null;
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        if (executorId != null && !userService.userExistsById(executorId)) {
            throw new NotFoundException("User with id " + executorId + " not found");
        }
        if (executorId != null && !participantsService.isParticipant(eventId, executorId)) {
            throw new ForbiddenException("User with id " + executorId +
                    " is not a participant of event with id " + eventId);
        }
        if (executorId != null && participantsService.isParticipantRole(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() + " cannot assign executors");
        }

        taskService.createTask(eventId, user, requestTaskInfo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ResponseTaskWithShoppingListsInfo> getTask(Authentication authentication,
                                                                     @PathVariable UUID eventId,
                                                                     @PathVariable UUID taskId) {
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }

        return ResponseEntity.ok(toResponseTaskWithShoppingListsInfo(
                eventId, user.getId(), taskService.getTaskById(eventId, taskId)));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<Void> patchTask(Authentication authentication,
                                                                       @PathVariable UUID eventId,
                                                                       @PathVariable UUID taskId,
                                                                       @RequestBody RequestTaskInfo requestTaskInfo) {
        User user = userService.userAuthentication(authentication);
        UUID executorId = requestTaskInfo.executor_id() != null ?
                UuidUtils.safeUUID(requestTaskInfo.executor_id()) : null;
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        if (executorId != null && !userService.userExistsById(executorId)) {
            throw new NotFoundException("User with id " + executorId + " not found");
        }
        if (executorId != null && !participantsService.isParticipant(eventId, executorId)) {
            throw new ForbiddenException("User with id " + executorId +
                    " is not a participant of event with id " + eventId);
        }
        if (executorId != null && participantsService.isParticipantRole(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() + " cannot assign executors " +
                    "to task with id: " + taskId);
        }
        if(taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.completed"))){
            throw new ConflictException("Task with id " + taskId + " is completed");
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !taskService.isAuthor(taskId, user.getId())){
            throw new ForbiddenException("User with id " + user.getId() + " cannot change " +
                    "task with id: " + taskId);
        }
        taskService.updateTask(taskId, requestTaskInfo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(Authentication authentication,
                                          @PathVariable UUID eventId,
                                          @PathVariable UUID taskId) {
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        if(taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.completed"))){
            throw new ConflictException("Task with id " + taskId + " is completed");
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !taskService.isAuthor(taskId, user.getId())){
            throw new ForbiddenException("User with id " + user.getId() + " cannot delete " +
                    "task with id: " + taskId);
        }

        taskService.deleteTask(eventId, taskId);
        return ResponseEntity.noContent().build();
    }


    private ResponseTaskWithShoppingListsInfo toResponseTaskWithShoppingListsInfo(UUID eventId, UUID userI, Task task) {
        return new ResponseTaskWithShoppingListsInfo(
                task.getTaskId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadlineDatetime(),
                task.getActualApprovalId() != null ? task.getTaskId().toString() : null,
                InfoEntityMapper.toUserInfo(task.getAuthor()),
                task.getExecutor() != null ? InfoEntityMapper.toUserInfo(task.getExecutor()) : null,
                task.getShoppingLists().stream()
                        .map(item -> InfoEntityMapper.toShoppingListInfo(item,
                                shoppingListsController.canEdit(eventId, item.getShoppingListId(), userI))).toList()
        );
    }

}
