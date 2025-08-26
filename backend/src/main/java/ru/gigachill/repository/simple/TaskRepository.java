package ru.gigachill.repository.simple;

import jakarta.annotation.Nullable;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;
import ru.gigachill.dto.TaskDTO;
import ru.gigachill.jooq.generated.enums.TaskStatus;
import ru.gigachill.jooq.generated.tables.Tasks;
import ru.gigachill.jooq.generated.tables.records.TasksRecord;

@Repository
@RequiredArgsConstructor
public class TaskRepository {
    private final DSLContext dsl;

    public Optional<TasksRecord> findById(UUID taskId) {
        return dsl.selectFrom(Tasks.TASKS).where(Tasks.TASKS.TASK_ID.eq(taskId)).fetchOptional();
    }

    public List<TasksRecord> findAllByEventId(UUID eventId) {
        return dsl.selectFrom(Tasks.TASKS).where(Tasks.TASKS.EVENT_ID.eq(eventId)).fetch();
    }

    public void save(TasksRecord taskRecord) {
        dsl.insertInto(Tasks.TASKS).set(taskRecord).execute();
    }

    public void update(TasksRecord taskRecord) {
        dsl.update(Tasks.TASKS)
                .set(taskRecord)
                .where(Tasks.TASKS.TASK_ID.eq(taskRecord.getTaskId()))
                .execute();
    }

    public void updateFromDTO(UUID taskId, TaskDTO dto) {
        Map<Field<?>, Object> updates = new HashMap<>();

        if (dto.getAuthor() != null) {
            updates.put(Tasks.TASKS.AUTHOR_ID, dto.getAuthor().getId());
        }
        if (dto.getExecutor() != null) {
            updates.put(Tasks.TASKS.EXECUTOR_ID, dto.getExecutor().getId());
        }
        if (dto.getTitle() != null) {
            updates.put(Tasks.TASKS.TITLE, dto.getTitle());
        }
        if (dto.getDescription() != null) {
            updates.put(Tasks.TASKS.DESCRIPTION, dto.getDescription());
        }
        if (dto.getStatus() != null) {
            updates.put(Tasks.TASKS.STATUS, TaskStatus.valueOf(dto.getStatus()));
        }
        if (dto.getDeadlineDatetime() != null) {
            updates.put(Tasks.TASKS.DEADLINE_DATETIME, dto.getDeadlineDatetime());
        }
        if (dto.getExecutorComment() != null) {
            updates.put(Tasks.TASKS.EXECUTOR_COMMENT, dto.getExecutorComment());
        }
        if (dto.getReviewerComment() != null) {
            updates.put(Tasks.TASKS.REVIEWER_COMMENT, dto.getReviewerComment());
        }

        if (!updates.isEmpty()) {
            dsl.update(Tasks.TASKS).set(updates).where(Tasks.TASKS.TASK_ID.eq(taskId)).execute();
        }
    }

    public boolean isAuthor(UUID taskId, UUID authorId) {
        return dsl.fetchExists(
                dsl.selectFrom(Tasks.TASKS)
                        .where(
                                Tasks.TASKS
                                        .TASK_ID
                                        .eq(taskId)
                                        .and(Tasks.TASKS.AUTHOR_ID.eq(authorId))));
    }

    public boolean exists(UUID eventId, UUID taskId) {
        return dsl.fetchExists(
                dsl.selectFrom(Tasks.TASKS)
                        .where(
                                Tasks.TASKS
                                        .EVENT_ID
                                        .eq(eventId)
                                        .and(Tasks.TASKS.TASK_ID.eq(taskId))));
    }

    public void deleteById(UUID taskId) {
        dsl.deleteFrom(Tasks.TASKS).where(Tasks.TASKS.TASK_ID.eq(taskId)).execute();
    }

    public boolean canExecute(UUID taskId, UUID userId) {
        return dsl.fetchExists(
                dsl.selectFrom(Tasks.TASKS)
                        .where(Tasks.TASKS.TASK_ID.eq(taskId))
                        .and(
                                Tasks.TASKS
                                        .EXECUTOR_ID
                                        .isNull()
                                        .or(Tasks.TASKS.EXECUTOR_ID.eq(userId))));
    }

    public void setExecutorIfNotAlready(UUID taskId, UUID executorId) {
        dsl.update(Tasks.TASKS)
                .set(Tasks.TASKS.EXECUTOR_ID, executorId)
                .set(Tasks.TASKS.STATUS, TaskStatus.in_progress)
                .where(
                        Tasks.TASKS
                                .TASK_ID
                                .eq(taskId)
                                .and(
                                        Tasks.TASKS
                                                .EXECUTOR_ID
                                                .isNull()
                                                .or(Tasks.TASKS.EXECUTOR_ID.eq(executorId))))
                .execute();
    }

    public void updateExecutor(UUID taskId, @Nullable UUID executorId) {
        dsl.update(Tasks.TASKS)
                .set(Tasks.TASKS.STATUS, TaskStatus.open)
                .set(Tasks.TASKS.EXECUTOR_ID, executorId)
                .set(Tasks.TASKS.EXECUTOR_COMMENT, (String) null)
                .set(Tasks.TASKS.REVIEWER_COMMENT, (String) null)
                .where(Tasks.TASKS.TASK_ID.eq(taskId))
                .execute();
    }

    public void setExecutorCommentAndMarkUnderReview(UUID taskId, String executorComment) {
        dsl.update(Tasks.TASKS)
                .set(Tasks.TASKS.EXECUTOR_COMMENT, executorComment)
                .set(Tasks.TASKS.STATUS, TaskStatus.under_review)
                .where(Tasks.TASKS.TASK_ID.eq(taskId))
                .execute();
    }

    public void setReviewerComment(UUID taskId, String reviewerComment) {
        dsl.update(Tasks.TASKS)
                .set(Tasks.TASKS.REVIEWER_COMMENT, reviewerComment)
                .where(Tasks.TASKS.TASK_ID.eq(taskId))
                .execute();
    }

    public void setStatus(UUID taskId, TaskStatus status) {
        dsl.update(Tasks.TASKS)
                .set(Tasks.TASKS.STATUS, status)
                .where(Tasks.TASKS.TASK_ID.eq(taskId))
                .execute();
    }
}
