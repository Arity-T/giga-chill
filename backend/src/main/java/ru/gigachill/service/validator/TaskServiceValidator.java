package ru.gigachill.service.validator;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.gigachill.repository.composite.TaskCompositeRepository;
import ru.gigachill.exception.ConflictException;
import ru.gigachill.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class TaskServiceValidator {
    private final TaskCompositeRepository taskCompositeRepository;
    private final ParticipantServiceValidator participantsServiceValidator;
    private final Environment env;

    public void checkIsExisted(UUID eventId, UUID taskId) {
        if (!taskCompositeRepository.isExisted(eventId, taskId)) {
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
        if (!taskCompositeRepository.canExecute(taskId, userId)) {
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
        if (!Objects.equals(taskCompositeRepository.getExecutorId(taskId), userId)) {
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
        if (Objects.equals(taskCompositeRepository.getExecutorId(taskId), userId)
                || participantsServiceValidator.isParticipantRole(eventId, userId)) {
            throw new ConflictException(
                    "User with id: " + userId + " cannot approve " + "task with id: " + taskId);
        }
    }

    public void checkTaskDeadline(OffsetDateTime eventEndDatetime, OffsetDateTime taskDeadline) {
        if (eventEndDatetime.isBefore(taskDeadline)) {
            throw new ConflictException(
                    "You cannot specify task due date: "
                            + taskDeadline
                            + " that is later than the end of the event: "
                            + eventEndDatetime);
        }
    }
}
