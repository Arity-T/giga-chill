package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.service.*;
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

    @GetMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ResponseTaskInfo>> getTasks(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);

        //Event validator
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        //Participant validator
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        return ResponseEntity.ok(taskService.getAllTasksFromEvent(eventId, user.getId()));
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

        //Event validator
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        //Event validator
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }

        //Participant validator
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        //User validator
        if (executorId != null && !userService.userExistsById(executorId)) {
            throw new NotFoundException("User with id " + executorId + " not found");
        }

        //Participant validator
        if (executorId != null && !participantsService.isParticipant(eventId, executorId)) {
            throw new ForbiddenException(
                    "User with id "
                            + executorId
                            + " is not a participant of event with id "
                            + eventId);
        }
        taskService.createTask(eventId, user, requestTaskInfo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    // ACCESS: owner, admin, participant
    public ResponseEntity<ResponseTaskWithShoppingListsInfo> getTask(
            Authentication authentication, @PathVariable UUID eventId, @PathVariable UUID taskId) {
        var user = userService.userAuthentication(authentication);

        //Event validator
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        //Task validator
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }

        //Participant validator
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        return ResponseEntity.ok(taskService.getTaskById(taskId, eventId, user.getId()));
    }

    @PatchMapping("/{taskId}")
    // ACCESS: owner, admin, participant(Если является автором)
    public ResponseEntity<Void> patchTask(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID taskId,
            @RequestBody RequestTaskInfo requestTaskInfo) {
        var user = userService.userAuthentication(authentication);

        //Event validator
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        //Event validator
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }

        //Task validator
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }

        //Participant validator
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        //Task validator
        if (taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.completed"))) {
            throw new ConflictException("Task with id " + taskId + " is completed");
        }

        //Participant validator
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

        //Event validator
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        //Event validator
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }

        //Task validator
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }

        //Participant validator
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        //User validator
        if (executorId != null && !userService.userExistsById(executorId)) {
            throw new NotFoundException("User with id " + executorId + " not found");
        }

        //Participant validator
        if (executorId != null && !participantsService.isParticipant(eventId, executorId)) {
            throw new ForbiddenException(
                    "User with id "
                            + executorId
                            + " is not a participant of event with id "
                            + eventId);
        }

        //Participant validator
        if (executorId != null && participantsService.isParticipantRole(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " cannot assign executors "
                            + "to task with id: "
                            + taskId);
        }

        //Participant validator
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

        //Event validator
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        //Event validator
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }
        //Task validator
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }

        //Participant validator
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        //Task validator
        if (taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.completed"))) {
            throw new ConflictException("Task with id " + taskId + " is completed");
        }

        //Participant validator
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !taskService.isAuthor(taskId, user.getId())) {
            throw new ForbiddenException(
                    "User with id " + user.getId() + " cannot change " + "task with id: " + taskId);
        }

        //Shopping list validator
        if (shoppingListsIds != null && !shoppingListsService.areExisted(shoppingListsIds)) {
            throw new NotFoundException(
                    "One or more of the resources involved were not found: " + body);
        }

        //Shopping list validator
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

        //Event validator
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        //Event validator
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }

        //Task validator
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }

        //Participant validator
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        //Task validator
        if (taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.completed"))) {
            throw new ConflictException("Task with id " + taskId + " is completed");
        }

        //Participant validator
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

        //Event validator
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        //Event validator
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }

        //Task validator
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }

        //Participant validator
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        //Task validator
        if (!taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.open"))) {
            throw new ConflictException("Task with id " + taskId + " is not open");
        }

        //Task validator
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

    @PostMapping("/{taskId}/send-for-review")
    // ACCESS: Только исполнитель
    public ResponseEntity<Void> postTaskForReview(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID taskId,
            @RequestBody Map<String, String> body) {
        var user = userService.userAuthentication(authentication);
        var executorComment = body.get("executor_comment");
        if (executorComment == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }

        //Event validator
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        //Event validator
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }

        //Task validator
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }

        //Participant validator
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        //Task validator
        if (!taskService.getTaskStatus(taskId).equals(env.getProperty("task_status.in_progress"))) {
            throw new ConflictException("Task with id " + taskId + " is not \"in progress\"");
        }

        //Task validator
        if (!taskService.getExecutorId(taskId).equals(user.getId())) {
            throw new ConflictException(
                    "User with id "
                            + user.getId()
                            + " cannot send "
                            + "task with id: "
                            + taskId
                            + " for review");
        }

        taskService.setExecutorComment(taskId, executorComment);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/review")
    // ACCESS: owner, admin(Если не исполнители)
    public ResponseEntity<Void> postTaskReview(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID taskId,
            @RequestBody Map<String, Object> body) {
        var user = userService.userAuthentication(authentication);
        var reviewerComment = (String) body.get("reviewer_comment");
        var isApproved = (Boolean) body.get("is_approved");
        if (reviewerComment == null || isApproved == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }

        //Event validator
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        //Event validator
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }

        //Task validator
        if (!taskService.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id " + taskId + " not found");
        }

        //Participant validator
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }

        //Task validator
        if (!taskService
                .getTaskStatus(taskId)
                .equals(env.getProperty("task_status.under_review"))) {
            throw new ConflictException("Task with id " + taskId + " is not \"under review\"");
        }

        //Task validator 
        if (taskService.getExecutorId(taskId).equals(user.getId())
                || participantsService.isParticipantRole(eventId, user.getId())) {
            throw new ConflictException(
                    "User with id "
                            + user.getId()
                            + " cannot approve "
                            + "task with id: "
                            + taskId);
        }

        taskService.setReviewerComment(taskId, reviewerComment, isApproved);
        return ResponseEntity.noContent().build();
    }
}
