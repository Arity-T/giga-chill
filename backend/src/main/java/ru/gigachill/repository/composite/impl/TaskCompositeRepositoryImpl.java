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
import ru.gigachill.dto.ShoppingItemDTO;
import ru.gigachill.dto.ParticipantDTO;
import ru.gigachill.dto.TaskDTO;
import ru.gigachill.dto.TaskWithShoppingListsDTO;
import ru.gigachill.dto.UserDTO;
import ru.gigachill.mapper.jooq.TasksRecordMapper;
import ru.gigachill.mapper.jooq.UsersRecordMapper;
import ru.gigachill.mapper.jooq.ShoppingRecordsMapper;
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
    private final ShoppingRecordsMapper shoppingRecordsMapper;

    /**
     * Maps a user ID to UserDTO by fetching user data from repository.
     * Returns null if userId is null or user not found.
     *
     * @param userId the user ID to map
     * @return UserDTO or null if user not found
     */
    private UserDTO mapUser(UUID userId) {
        if (userId == null) return null;
        UsersRecord record = userRepository.findById(userId).orElse(null);
        return record == null ? null : usersRecordMapper.toUserDTO(record);
    }

    /**
     * Creates TaskWithShoppingListsDTO from TasksRecord with resolved author and executor.
     * This method coordinates data fetching and mapping for complete task representation.
     *
     * @param record the task record from database
     * @param shoppingLists the shopping lists associated with the task
     * @return complete TaskWithShoppingListsDTO with resolved user data
     */
    private TaskWithShoppingListsDTO toTaskWithShoppingListsDTO(
            TasksRecord record, List<ShoppingListDTO> shoppingLists) {
        UserDTO author = mapUser(record.getAuthorId());
        UserDTO executor = mapUser(record.getExecutorId());
        return tasksRecordMapper.toTaskWithShoppingListsDTO(record, shoppingLists, author, executor);
    }

    @Override
    public List<TaskDTO> getAllTasksFromEvent(UUID eventId) {
        return taskRepository.findAllByEventId(eventId).stream()
                .map(this::mapTaskRecordToTaskDTO)
                .toList();
    }

    /**
     * Maps TasksRecord to TaskDTO with resolved author and executor.
     *
     * @param record the task record from database
     * @return TaskDTO with resolved user data
     */
    private TaskDTO mapTaskRecordToTaskDTO(TasksRecord record) {
        TaskDTO dto = tasksRecordMapper.toTaskDTO(record);
        dto.setAuthor(mapUser(record.getAuthorId()));
        dto.setExecutor(mapUser(record.getExecutorId()));
        return dto;
    }

    @Override
    public TaskWithShoppingListsDTO getTaskById(UUID taskId) {
        TasksRecord taskRecord = taskRepository.findById(taskId).orElse(null);
        if (taskRecord == null) return null;

        List<ShoppingListDTO> shoppingLists = getShoppingListsForTask(taskId);
        return toTaskWithShoppingListsDTO(taskRecord, shoppingLists);
    }

    /**
     * Retrieves shopping lists for a specific task with their items and consumers.
     * <p>
     * This method aggregates data from multiple sources:
     * 1. Shopping list records associated with the task
     * 2. Shopping items within each list
     * 3. Consumer participants assigned to each list
     * <p>
     * The result provides a complete view of all shopping data related to the task.
     */
    @Override
    public List<ShoppingListDTO> getShoppingListsForTask(UUID taskId) {
        return shoppingListRepository.findByTaskId(taskId).stream()
                .map(record -> {
                    List<ShoppingItemDTO> items = shoppingItemRepository.findByShoppingListId(record.getShoppingListId()).stream()
                            .map(shoppingRecordsMapper::toShoppingItemDTO)
                            .toList();
                    
                    List<ParticipantDTO> consumers = shoppingListCompositeRepository.getConsumersForShoppingList(record.getShoppingListId(), record.getEventId());
                    
                    return shoppingRecordsMapper.toShoppingListDTOWithDetails(record, items, consumers);
                })
                .toList();
    }

    /**
     * Creates a new task for the specified event and associates it with the given shopping lists.
     * <p>
     * This method performs an atomic operation that:
     * 1. Creates the task record in the database
     * 2. Links all specified shopping lists to the newly created task
     */
    @Transactional
    @Override
    public void createTask(UUID eventId, TaskDTO taskDTO, List<UUID> shoppingListsIds) {
        taskRepository.save(tasksRecordMapper.toTasksRecord(taskDTO, eventId));

        // Link shopping lists to the newly created task
        for (UUID shoppingListId : shoppingListsIds) {
            shoppingListRepository.updateTaskId(shoppingListId, taskDTO.getTaskId());
        }
    }

    @Transactional
    @Override
    public void updateTask(UUID taskId, TaskDTO taskDTO) {
        taskRepository.updateFromDTO(taskId, taskDTO);
    }

    @Transactional
    @Override
    public void startExecuting(UUID taskId, UUID userId) {
        taskRepository.setExecutorIfNotAlready(taskId, userId);
    }

    /**
     * Deletes the specified task and cleans up associated data.
     * <p>
     * This method performs a cascading delete operation:
     * 1. Resets status of all shopping items in associated lists (marks as unpurchased)
     * 2. Deletes the task record (shopping list associations are automatically nullified by DB constraints)
     */
    @Transactional
    @Override
    public void deleteTask(UUID taskId) {
        List<UUID> shoppingListIds = shoppingListRepository.findIdsByTaskId(taskId);

        shoppingItemRepository.resetAllStatusByListIds(shoppingListIds);

        // Shopping list associations are automatically nullified by database constraints
        taskRepository.deleteById(taskId);
    }

    @Override
    public boolean isAuthor(UUID taskId, UUID userId) {
        return taskRepository.isAuthor(taskId, userId);
    }

    @Override
    public String getTaskStatus(UUID taskId) {
        Optional<TasksRecord> taskRecordOpt = taskRepository.findById(taskId);

        if (taskRecordOpt.isEmpty()) {
            return null;
        }

        TasksRecord taskRecord = taskRecordOpt.get();

        return taskRecord.getStatus().getLiteral();
    }

    @Override
    public boolean isExisted(UUID eventId, UUID taskId) {
        return taskRepository.exists(eventId, taskId);
    }

    @Override
    public boolean canExecute(UUID taskId, UUID userId) {
        return taskRepository.canExecute(taskId, userId);
    }

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
     * <p>
     * When the executor changes, this method also resets the status of all shopping items
     * in associated lists to ensure data consistency (items become unpurchased when task
     * execution is reassigned).
     */
    @Transactional
    @Override
    public void updateExecutor(UUID taskId, @Nullable UUID executorId) {
        taskRepository.updateExecutor(taskId, executorId);

        List<UUID> listIds = shoppingListRepository.findIdsByTaskId(taskId);

        // Reset item statuses when executor changes to maintain data consistency
        for (UUID listId : listIds) {
            shoppingItemRepository.resetAllStatusByListId(listId);
        }
    }

    /**
     * Updates the set of shopping lists associated with the specified task.
     * <p>
     * This method implements a diff-based update strategy:
     * 1. Compares current shopping list associations with the new desired state
     * 2. Removes associations that are no longer needed (resets item statuses)
     * 3. Adds new associations as needed
     * 4. When shoppingLists is null, removes all associations
     */
    @Transactional
    @Override
    public void updateShoppingLists(UUID taskId, @Nullable List<UUID> shoppingLists) {
        List<UUID> currentIds = shoppingListRepository.findIdsByTaskId(taskId);

        if (shoppingLists == null) {
            // Clear all associations and reset item statuses
            shoppingListRepository.detachAllFromTask(taskId);
            for (UUID listId : currentIds) {
                shoppingItemRepository.resetAllStatusByListId(listId);
            }
            return;
        }

        // Calculate differences: what to remove and what to add
        Set<UUID> toRemove = new HashSet<>(currentIds);
        shoppingLists.forEach(toRemove::remove);

        Set<UUID> toAdd = new HashSet<>(shoppingLists);
        currentIds.forEach(toAdd::remove);

        // Remove old associations and reset item statuses
        for (UUID listId : toRemove) {
            shoppingListRepository.updateTaskId(listId, null);
            shoppingItemRepository.resetAllStatusByListId(listId);
        }

        // Add new associations
        for (UUID listId : toAdd) {
            shoppingListRepository.updateTaskId(listId, taskId);
        }
    }

    @Transactional
    @Override
    public void setExecutorComment(UUID taskId, String executorComment) {
        taskRepository.setExecutorCommentAndMarkUnderReview(taskId, executorComment);
    }

    /**
     * Updates the reviewer's comment and approval status for the specified task.
     * <p>
     * This method implements the task review workflow:
     * - When approved: task status becomes "completed"
     * - When rejected: task status reverts to "in_progress" for rework
     * - Reviewer comment is always updated regardless of approval status
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
