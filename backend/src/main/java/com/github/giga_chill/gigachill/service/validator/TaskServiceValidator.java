package com.github.giga_chill.gigachill.service.validator;

import com.github.giga_chill.gigachill.data.access.object.TaskDAO;
import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskServiceValidator {
    private final TaskDAO taskDAO;
    private final ParticipantsServiceValidator participantsServiceValidator;
    private final Environment env;

    public void checkIsExisted(UUID eventId, UUID taskId) {
        if (!taskDAO.isExisted(eventId, taskId)) {
            throw new NotFoundException("Task with id: " + taskId + " not found");
        }
    }

    public void checkNotCompletedStatus(UUID taskId, String taskStatus) {
        if (taskStatus.equals(env.getProperty("task_status.completed"))) {
            throw new ConflictException("Task with id: " + taskId + " is completed");
        }
    }

    public void checkOpenStatus(UUID taskId, String taskStatus) {
        if (!taskStatus.equals(env.getProperty("task_status.open"))) {
            throw new ConflictException("Task with id: " + taskId + " is not open");
        }
    }

    public void checkExecutionOpportunity(UUID taskId, UUID userId) {
        if (!taskDAO.canExecute(taskId, userId)) {
            throw new ConflictException(
                    "User with id: " + userId + " cannot execute " + "task with id: " + taskId);
        }
    }

    public void checkInProgressStatus(UUID taskId, String taskStatus) {
        if (!taskStatus.equals(env.getProperty("task_status.in_progress"))) {
            throw new ConflictException("Task with id: " + taskId + " is not \"in progress\"");
        }
    }

    public void checkOpportunityToSentTaskToReview(UUID taskId, UUID userId) {
        if (!taskDAO.getExecutorId(taskId).equals(userId)) {
            throw new ConflictException(
                    "User with id: "
                            + userId
                            + " cannot send "
                            + "task with id: "
                            + taskId
                            + " for review");
        }
    }

    public void checkUnderReviewStatus(UUID taskId, String taskStatus) {
        if (!taskStatus.equals(env.getProperty("task_status.under_review"))) {
            throw new ConflictException("Task with id: " + taskId + " is not \"under review\"");
        }
    }

    public void checkOpportunityToApproveTask(UUID eventId, UUID taskId, UUID userId) {
        if (Objects.equals(taskDAO.getExecutorId(taskId), userId)
                || participantsServiceValidator.isParticipantRole(eventId, userId)) {
            throw new ConflictException(
                    "User with id: " + userId + " cannot approve " + "task with id: " + taskId);
        }
    }
}
