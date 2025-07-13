package com.github.giga_chill.gigachill.repository;

import com.github.giga_chill.gigachill.data.transfer.object.TaskDTO;
import com.github.giga_chill.jooq.generated.enums.TaskStatus;
import com.github.giga_chill.jooq.generated.tables.Tasks;
import com.github.giga_chill.jooq.generated.tables.records.TasksRecord;
import java.time.OffsetDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

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

        updates.put(Tasks.TASKS.AUTHOR_ID, dto.author().id());

        if (dto.executor() != null) {
            updates.put(Tasks.TASKS.EXECUTOR_ID, dto.executor().id());
        }
        if (dto.title() != null) {
            updates.put(Tasks.TASKS.TITLE, dto.title());
        }
        if (dto.description() != null) {
            updates.put(Tasks.TASKS.DESCRIPTION, dto.description());
        }
        if (dto.status() != null) {
            updates.put(Tasks.TASKS.STATUS, TaskStatus.valueOf(dto.status()));
        }
        if (dto.deadlineDatetime() != null) {
            updates.put(
                    Tasks.TASKS.DEADLINE_DATETIME, OffsetDateTime.parse(dto.deadlineDatetime()));
        }
        if (dto.actualApprovalId() != null) {
            updates.put(Tasks.TASKS.ACTUAL_APPROVAL_ID, dto.actualApprovalId());
        }

        dsl.update(Tasks.TASKS).set(updates).where(Tasks.TASKS.TASK_ID.eq(taskId)).execute();
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
                                                .or(Tasks.TASKS.EXECUTOR_ID.ne(executorId))))
                .execute();
    }
}
