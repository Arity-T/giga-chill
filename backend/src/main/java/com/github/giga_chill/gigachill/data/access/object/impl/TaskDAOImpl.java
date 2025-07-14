package com.github.giga_chill.gigachill.data.access.object.impl;

import com.github.giga_chill.gigachill.data.access.object.TaskDAO;
import com.github.giga_chill.gigachill.data.transfer.object.ShoppingListDTO;
import com.github.giga_chill.gigachill.data.transfer.object.TaskDTO;
import com.github.giga_chill.gigachill.data.transfer.object.TaskWithShoppingListsDTO;
import com.github.giga_chill.gigachill.data.transfer.object.UserDTO;
import com.github.giga_chill.gigachill.repository.ShoppingListRepository;
import com.github.giga_chill.gigachill.repository.TaskRepository;
import com.github.giga_chill.gigachill.repository.UserRepository;
import com.github.giga_chill.jooq.generated.enums.TaskStatus;
import com.github.giga_chill.jooq.generated.tables.records.TasksRecord;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import jakarta.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskDAOImpl implements TaskDAO {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListDAOImpl shoppingListDAOImpl;

    private UserDTO getAuthorDTO(UUID authorId) {
        // Автор - обязательное поле
        UsersRecord authorRecord = userRepository.findById(authorId).get();

        return new UserDTO(
                authorRecord.getUserId(), authorRecord.getLogin(), authorRecord.getName());
    }

    private UserDTO getExecutorDTO(UUID executorId) {
        // Исполнитель - обрабатываем как nullable
        UserDTO executor = null;
        if (executorId != null) {
            UsersRecord executorRecord = userRepository.findById(executorId).orElse(null);
            if (executorRecord != null) {
                executor =
                        new UserDTO(
                                executorRecord.getUserId(),
                                executorRecord.getLogin(),
                                executorRecord.getName());
            }
        }
        return executor;
    }

    private TaskDTO convertToTaskDTO(TasksRecord record) {
        return new TaskDTO(
                record.getTaskId(),
                record.getTitle(),
                record.getDescription(),
                record.getStatus().getLiteral(),
                record.getDeadlineDatetime().toString(),
                record.getActualApprovalId(),
                getAuthorDTO(record.getAuthorId()),
                getExecutorDTO(record.getExecutorId()));
    }

    private TaskWithShoppingListsDTO convertToTaskWithShoppingListsDTO(
            TasksRecord record, List<ShoppingListDTO> shoppingLists) {
        return new TaskWithShoppingListsDTO(
                record.getTaskId(),
                record.getTitle(),
                record.getDescription(),
                record.getStatus().getLiteral(),
                record.getDeadlineDatetime().toString(),
                record.getActualApprovalId(),
                getAuthorDTO(record.getAuthorId()),
                getExecutorDTO(record.getExecutorId()),
                shoppingLists);
    }

    /**
     * Retrieves all tasks associated with the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a {@link List} of {@link TaskDTO} representing all tasks in the event; empty list if
     *     none found
     */
    @Override
    public List<TaskDTO> getAllTasksFromEvent(UUID eventId) {
        return taskRepository.findAllByEventId(eventId).stream()
                .map(this::convertToTaskDTO)
                .toList();
    }

    /**
     * Retrieves a task by its unique identifier, including its associated shopping lists.
     *
     * @param taskId the unique identifier of the task
     * @return a {@link TaskWithShoppingListsDTO} containing task details and its shopping list
     *     references
     */
    @Override
    public TaskWithShoppingListsDTO getTaskById(UUID taskId) {
        TasksRecord taskRecord = taskRepository.findById(taskId).orElse(null);
        if (taskRecord == null) return null;

        List<ShoppingListDTO> shoppingLists =
                shoppingListDAOImpl.getAllShoppingListsFromEvent(taskRecord.getEventId()).stream()
                        .filter(list -> taskId.equals(list.taskId()))
                        .toList();

        return convertToTaskWithShoppingListsDTO(taskRecord, shoppingLists);
    }

    /**
     * Creates a new task for the specified event and associates it with the given shopping lists.
     *
     * @param eventId the unique identifier of the event in which to create the task
     * @param taskDTO the {@link TaskDTO} containing details of the task to create
     * @param shoppingListsIds a {@link List} of {@link UUID} values representing shopping lists to
     *     attach to the task
     */
    @Override
    public void createTask(UUID eventId, TaskDTO taskDTO, List<UUID> shoppingListsIds) {
        taskRepository.save(
                new TasksRecord(
                        taskDTO.taskId(),
                        eventId,
                        taskDTO.author().id(),
                        taskDTO.executor() != null ? taskDTO.executor().id() : null,
                        taskDTO.title(),
                        taskDTO.description(),
                        taskDTO.status() != null ? TaskStatus.valueOf(taskDTO.status()) : null,
                        taskDTO.deadlineDatetime() != null
                                ? OffsetDateTime.parse(taskDTO.deadlineDatetime())
                                : null,
                        taskDTO.actualApprovalId()));

        // Привязываем shopping lists к задаче
        for (UUID shoppingListId : shoppingListsIds) {
            shoppingListRepository.updateTaskId(shoppingListId, taskDTO.taskId());
        }
    }

    /**
     * Updates an existing task’s data and rebinds it to the specified shopping lists.
     *
     * @param taskId the unique identifier of the task to update
     * @param taskDTO the {@link TaskDTO} containing the updated task information
     * @param shoppingListsIds a {@link List} of {@link UUID} values representing the new set of
     *     shopping lists to attach
     */
    @Override
    public void updateTask(UUID taskId, TaskDTO taskDTO, List<UUID> shoppingListsIds) {
        taskRepository.updateFromDTO(taskId, taskDTO);

        // Обновляем связанные списки покупок, только если они явно переданы
        if (shoppingListsIds != null) {
            // Убираем старые связи
            shoppingListRepository.detachFromTask(taskId);

            // Привязываем переданные списки
            for (UUID shoppingListId : shoppingListsIds) {
                shoppingListRepository.updateTaskId(shoppingListId, taskId);
            }
        }
    }

    /**
     * Marks a task as started by a specific user, indicating execution has begun.
     *
     * @param taskId the unique identifier of the task to start
     * @param userId the unique identifier of the user executing the task
     */
    @Override
    public void startExecuting(UUID taskId, UUID userId) {
        taskRepository.setExecutorIfNotAlready(taskId, userId);
    }

    /**
     * Deletes the specified task.
     *
     * @param taskId the unique identifier of the task to delete
     */
    @Override
    public void deleteTask(UUID taskId) {
        taskRepository.deleteById(taskId);
    }

    /**
     * Checks if the given user is the author (creator) of the task.
     *
     * @param taskId the unique identifier of the task
     * @param userId the unique identifier of the user to check
     * @return {@code true} if the user is the author; {@code false} otherwise
     */
    @Override
    public boolean isAuthor(UUID taskId, UUID userId) {
        return taskRepository.isAuthor(taskId, userId);
    }

    /**
     * Retrieves the current status of the task (e.g., "pending", "in_progress", "completed").
     *
     * @param taskId the unique identifier of the task
     * @return the status string of the task
     */
    @Override
    public String getTaskStatus(UUID taskId) {
        Optional<TasksRecord> taskRecordOpt = taskRepository.findById(taskId);

        if (taskRecordOpt.isEmpty()) {
            return null;
        }

        TasksRecord taskRecord = taskRecordOpt.get();

        return taskRecord.getStatus().getLiteral();
    }

    /**
     * Checks whether a task with the given identifier exists within the specified event context.
     *
     * @param eventId the unique identifier of the event
     * @param taskId the unique identifier of the task
     * @return {@code true} if the task exists within the event; {@code false} otherwise
     */
    @Override
    public boolean isExisted(UUID eventId, UUID taskId) {
        return taskRepository.exists(eventId, taskId);
    }

    /**
     * Determines if a user has permission to execute the specified task.
     *
     * @param taskId the unique identifier of the task
     * @param userId the unique identifier of the user
     * @return {@code true} if the user can execute the task(if he is executor or executor is null);
     *     {@code false} otherwise
     */
    @Override
    public boolean canExecute(UUID taskId, UUID userId) {
        return taskRepository.canExecute(taskId, userId);
    }

    @Nullable
    @Override
    // TODO реализовать метод
    public UUID getExecutorId(UUID taskId) {
        return null;
    }
}
