package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.service.*;
import com.github.giga_chill.gigachill.web.info.RequestTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskWithShoppingListsInfo;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("events/{eventId}/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final UserService userService;
    private final TaskService taskService;

    @GetMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ResponseTaskInfo>> getTasks(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);
        return ResponseEntity.ok(taskService.getAllTasksFromEvent(eventId, user.getId()));
    }

    @PostMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> postTask(
            Authentication authentication,
            @PathVariable UUID eventId,
            @RequestBody RequestTaskInfo requestTaskInfo) {
        var user = userService.userAuthentication(authentication);
        taskService.createTask(eventId, user, requestTaskInfo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    // ACCESS: owner, admin, participant
    public ResponseEntity<ResponseTaskWithShoppingListsInfo> getTask(
            Authentication authentication, @PathVariable UUID eventId, @PathVariable UUID taskId) {
        var user = userService.userAuthentication(authentication);
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
        taskService.updateTask(eventId, taskId, user.getId(), requestTaskInfo);
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
        taskService.updateExecutor(taskId, eventId, user.getId(), body);
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
        taskService.updateShoppingLists(taskId, eventId, user.getId(), body);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    // ACCESS: owner, admin, participant(Если является автором)
    public ResponseEntity<Void> deleteTask(
            Authentication authentication, @PathVariable UUID eventId, @PathVariable UUID taskId) {
        var user = userService.userAuthentication(authentication);
        taskService.deleteTask(taskId, eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/take-in-work")
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> postExecutorToTask(
            Authentication authentication, @PathVariable UUID eventId, @PathVariable UUID taskId) {
        var user = userService.userAuthentication(authentication);
        taskService.startExecuting(taskId, user.getId(), eventId);
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
        taskService.setExecutorComment(taskId, eventId, user.getId(), body);
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
        taskService.setReviewerComment(taskId, body, eventId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
