package ru.gigachill.repository.composite.impl;

import com.github.giga_chill.jooq.generated.enums.TaskStatus;
import com.github.giga_chill.jooq.generated.tables.records.TasksRecord;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import jakarta.annotation.Nullable;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.gigachill.repository.composite.ShoppingListCompositeRepository;
import ru.gigachill.repository.composite.TaskCompositeRepository;
import ru.gigachill.dto.ShoppingListDTO;
import ru.gigachill.dto.TaskDTO;
import ru.gigachill.dto.TaskWithShoppingListsDTO;
import ru.gigachill.dto.UserDTO;
import ru.gigachill.mapper.jooq.TasksRecordMapper;
import ru.gigachill.mapper.jooq.UsersRecordMapper;
import ru.gigachill.repository.simple.ShoppingItemRepository;
import ru.gigachill.repository.simple.ShoppingListRepository;
import ru.gigachill.repository.simple.TaskRepository;
import ru.gigachill.repository.simple.UserRepository;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class TaskCompositeRepositoryImpl implements TaskCompositeRepository {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListCompositeRepository shoppingListCompositeRepository;
    private final ShoppingItemRepository shoppingItemRepository;
    private final TasksRecordMapper tasksRecordMapper;
    private final UsersRecordMapper usersRecordMapper;

    private UserDTO mapUser(UUID userId) {
        if (userId == null) return null;
        UsersRecord record = userRepository.findById(userId).orElse(null);
        return record == null ? null : usersRecordMapper.toUserDTO(record);
    }

    private TaskWithShoppingListsDTO toTaskWithShoppingListsDTO(
            TasksRecord record, List<ShoppingListDTO> shoppingLists) {
        TaskDTO dto = tasksRecordMapper.toTaskDTO(record);
        dto.setAuthor(mapUser(record.getAuthorId()));
        dto.setExecutor(mapUser(record.getExecutorId()));
        return tasksRecordMapper.toTaskWithShoppingListsDTO(dto, shoppingLists);
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
                .map(record -> {
                    TaskDTO dto = tasksRecordMapper.toTaskDTO(record);
                    dto.setAuthor(mapUser(record.getAuthorId()));
                    dto.setExecutor(mapUser(record.getExecutorId()));
                    return dto;
                })
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
                shoppingListCompositeRepository.getAllShoppingListsFromEvent(taskRecord.getEventId()).stream()
                        .filter(list -> taskId.equals(list.getTaskId()))
                        .toList();

        return toTaskWithShoppingListsDTO(taskRecord, shoppingLists);
    }

    /**
     * Creates a new task for the specified event and associates it with the given shopping lists.
     *
     * @param eventId the unique identifier of the event in which to create the task
     * @param taskDTO the {@link TaskDTO} containing details of the task to create
     * @param shoppingListsIds a {@link List} of {@link UUID} values representing shopping lists to
     *     attach to the task
     */
    @Transactional
    @Override
    public void createTask(UUID eventId, TaskDTO taskDTO, List<UUID> shoppingListsIds) {
        taskRepository.save(tasksRecordMapper.toTasksRecord(taskDTO, eventId));

        // Привязываем shopping lists к задаче
        for (UUID shoppingListId : shoppingListsIds) {
            shoppingListRepository.updateTaskId(shoppingListId, taskDTO.getTaskId());
        }
    }

    /**
     * Updates an existing task’s data.
     *
     * @param taskId the unique identifier of the task to update
     * @param taskDTO the {@link TaskDTO} containing the updated task information
     */
    @Transactional
    @Override
    public void updateTask(UUID taskId, TaskDTO taskDTO) {
        taskRepository.updateFromDTO(taskId, taskDTO);
    }

    /**
     * Marks a task as started by a specific user, indicating execution has begun.
     *
     * @param taskId the unique identifier of the task to start
     * @param userId the unique identifier of the user executing the task
     */
    @Transactional
    @Override
    public void startExecuting(UUID taskId, UUID userId) {
        taskRepository.setExecutorIfNotAlready(taskId, userId);
    }

    /**
     * Deletes the specified task.
     *
     * @param taskId the unique identifier of the task to delete
     */
    @Transactional
    @Override
    public void deleteTask(UUID taskId) {
        List<UUID> shoppingListIds = shoppingListRepository.findIdsByTaskId(taskId);

        shoppingItemRepository.resetAllStatusByListIds(shoppingListIds);

        // Связи у ShoppingLists автоматически выставляются в null на уровне СУБД
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

    /**
     * Retrieves the identifier of the user who is currently executing the specified task.
     *
     * @param taskId the unique identifier of the task
     * @return the {@link UUID} of the executor user if one is assigned; {@code null} if the task
     *     has not been started or no executor is set
     */
    @Nullable
    @Override
    public UUID getExecutorId(UUID taskId) {
        TasksRecord task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return null;
        }

        return task.getExecutorId();
    }

    /**
     * Assigns or unassigns the executor for the specified task.
     *
     * @param taskId the unique identifier of the task to update
     * @param executorId the {@link UUID} of the user who will execute the task; may be {@code null}
     *     to clear any existing executor
     */
    @Transactional
    @Override
    public void updateExecutor(UUID taskId, @Nullable UUID executorId) {
        taskRepository.updateExecutor(taskId, executorId);

        List<UUID> listIds = shoppingListRepository.findIdsByTaskId(taskId);

        // Отвязываем и сбрасываем статус товаров
        for (UUID listId : listIds) {
            shoppingItemRepository.resetAllStatusByListId(listId);
        }
    }

    /**
     * Updates the set of shopping lists associated with the specified task.
     *
     * @param taskId the unique identifier of the task to update
     * @param shoppingLists a {@link List} of {@link UUID} values representing new shopping list IDs
     *     to bind; may be {@code null} to clear all associations If the list is unlinked, its
     *     status becomes "Unassigned". And all the purchases become not purchased.
     */
    @Transactional
    @Override
    public void updateShoppingLists(UUID taskId, @Nullable List<UUID> shoppingLists) {
        // Текущие списки, привязанные к задаче
        List<UUID> currentIds = shoppingListRepository.findIdsByTaskId(taskId);

        if (shoppingLists == null) {
            // 1) Убираем все связи
            shoppingListRepository.detachAllFromTask(taskId);
            // 2) Сбрасываем статус у всех товаров в этих списках
            for (UUID listId : currentIds) {
                shoppingItemRepository.resetAllStatusByListId(listId);
            }
            return;
        }

        // Кого надо отвязать: есть в current, но нет в new
        Set<UUID> toRemove = new HashSet<>(currentIds);
        shoppingLists.forEach(toRemove::remove);

        // Кого надо привязать: есть в new, но нет в current
        Set<UUID> toAdd = new HashSet<>(shoppingLists);
        currentIds.forEach(toAdd::remove);

        // Отвязываем и сбрасываем статус товаров
        for (UUID listId : toRemove) {
            shoppingListRepository.updateTaskId(listId, null);
            shoppingItemRepository.resetAllStatusByListId(listId);
        }

        // Привязываем новые
        for (UUID listId : toAdd) {
            shoppingListRepository.updateTaskId(listId, taskId);
        }
    }

    /**
     * Updates the comment provided by the executor for the specified task.
     *
     * @param taskId the unique identifier of the task to update
     * @param executorComment the comment text from the executor
     */
    @Transactional
    @Override
    public void setExecutorComment(UUID taskId, String executorComment) {
        taskRepository.setExecutorCommentAndMarkUnderReview(taskId, executorComment);
    }

    /**
     * Updates the reviewer's comment and approval status for the specified task.
     *
     * @param taskId the unique identifier of the task to update
     * @param reviewerComment the comment text from the reviewer; may be empty or null to clear
     *     existing comment
     * @param isApproved {@code true} if the reviewer approves the task - The task status becomes
     *     "completed"; {@code false} otherwise - The task status becomes "in_progress"
     */
    @Transactional
    @Override
    public void setReviewerComment(UUID taskId, String reviewerComment, boolean isApproved) {
        taskRepository.setReviewerComment(taskId, reviewerComment);
        if (isApproved) {
            taskRepository.setStatus(taskId, TaskStatus.completed);
        } else {
            taskRepository.setStatus(taskId, TaskStatus.in_progress);
        }
    }
}
