package ru.gigachill.web.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.gigachill.service.TaskService;
import ru.gigachill.service.UserService;
import ru.gigachill.web.api.TasksApi;
import ru.gigachill.web.api.model.*;

@RestController
@RequiredArgsConstructor
public class TasksController implements TasksApi {
    private final UserService userService;
    private final TaskService taskService;

    @Override
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> createTask(UUID eventId, TaskCreate taskCreate) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        taskService.createTask(eventId, user, taskCreate);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant(Если является автором)
    public ResponseEntity<Void> deleteTask(UUID eventId, UUID taskId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        taskService.deleteTask(taskId, eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant
    public ResponseEntity<TaskWithShoppingLists> getTask(UUID eventId, UUID taskId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(taskService.getTaskById(taskId, eventId, user.getId()));
    }

    @Override
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<Task>> getTasks(UUID eventId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(taskService.getAllTasksFromEvent(eventId, user.getId()));
    }

    @Override
    // ACCESS: owner, admin(Если не исполнители)
    public ResponseEntity<Void> reviewTask(
            UUID eventId, UUID taskId, TaskReviewRequest taskReviewRequest) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        taskService.setReviewerComment(taskId, taskReviewRequest, eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: Только исполнитель
    public ResponseEntity<Void> sendTaskForReview(
            UUID eventId, UUID taskId, TaskSendForReviewRequest taskSendForReviewRequest) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        taskService.setExecutorComment(taskId, eventId, user.getId(), taskSendForReviewRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant(Если является автором)
    public ResponseEntity<Void> setTaskExecutor(
            UUID eventId, UUID taskId, TaskSetExecutor taskSetExecutor) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        taskService.updateExecutor(taskId, eventId, user.getId(), taskSetExecutor);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant(Если является автором)
    public ResponseEntity<Void> setTaskShoppingLists(UUID eventId, UUID taskId, List<UUID> UUID) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        taskService.updateShoppingLists(taskId, eventId, user.getId(), UUID);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> takeTaskInWork(UUID eventId, UUID taskId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        taskService.startExecuting(taskId, user.getId(), eventId);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant(Если является автором)
    public ResponseEntity<Void> updateTask(UUID eventId, UUID taskId, TaskUpdate taskUpdate) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        taskService.updateTask(eventId, taskId, user.getId(), taskUpdate);
        return ResponseEntity.noContent().build();
    }
}
