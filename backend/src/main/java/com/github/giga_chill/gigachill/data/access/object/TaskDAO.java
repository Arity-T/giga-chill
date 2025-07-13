package com.github.giga_chill.gigachill.data.access.object;

import com.github.giga_chill.gigachill.data.transfer.object.TaskDTO;
import com.github.giga_chill.gigachill.data.transfer.object.TaskWithShoppingListsDTO;

import java.util.List;
import java.util.UUID;

/**
 * Data Access Object (DAO) interface for managing tasks within events.
 * <p>
 * Provides methods to perform CRUD operations, execution control, and permission checks on tasks.
 * </p>
 */
public interface TaskDAO {

    /**
     * Retrieves all tasks associated with the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a {@link List} of {@link TaskDTO} representing all tasks in the event; empty list if none found
     */
    List<TaskDTO> getAllTasksFromEvent(UUID eventId);

    /**
     * Retrieves a task by its unique identifier, including its associated shopping lists.
     *
     * @param taskId the unique identifier of the task
     * @return a {@link TaskWithShoppingListsDTO} containing task details and its shopping list references
     */
    TaskWithShoppingListsDTO getTaskById(UUID taskId);

    /**
     * Creates a new task under the given event, with optional associated shopping lists.
     *
     * @param eventId                  the unique identifier of the event
     * @param taskWithShoppingListsDTO the {@link TaskWithShoppingListsDTO} containing task data and shopping lists
     */
    void createTask(UUID eventId, TaskWithShoppingListsDTO taskWithShoppingListsDTO);

    /**
     * Updates an existing task's details and its associated shopping lists.
     *
     * @param taskId                   the unique identifier of the task to update
     * @param taskWithShoppingListsDTO the updated {@link TaskWithShoppingListsDTO} data
     */
    void updateTask(UUID taskId, TaskWithShoppingListsDTO taskWithShoppingListsDTO);

    /**
     * Marks a task as started by a specific user, indicating execution has begun.
     *
     * @param taskId the unique identifier of the task to start
     * @param userId the unique identifier of the user executing the task
     */
    void startExecuting(UUID taskId, UUID userId);

    /**
     * Deletes the specified task.
     *
     * @param taskId the unique identifier of the task to delete
     */
    void deleteTask(UUID taskId);

    /**
     * Checks if the given user is the author (creator) of the task.
     *
     * @param taskId the unique identifier of the task
     * @param userId the unique identifier of the user to check
     * @return {@code true} if the user is the author; {@code false} otherwise
     */
    boolean isAuthor(UUID taskId, UUID userId);

    /**
     * Retrieves the current status of the task (e.g., "pending", "in_progress", "completed").
     *
     * @param taskId the unique identifier of the task
     * @return the status string of the task
     */
    String getTaskStatus(UUID taskId);

    /**
     * Checks whether a task with the given identifier exists within the specified event context.
     *
     * @param eventId the unique identifier of the event
     * @param taskId  the unique identifier of the task
     * @return {@code true} if the task exists within the event; {@code false} otherwise
     */
    boolean isExisted(UUID eventId, UUID taskId);

    /**
     * Determines if a user has permission to execute the specified task.
     *
     * @param taskId the unique identifier of the task
     * @param userId the unique identifier of the user
     * @return {@code true} if the user can execute the task(if he is executor or executor is null);
     * {@code false} otherwise
     */
    boolean canExecute(UUID taskId, UUID userId);

    /**
     * Determines whether all products in this list are purchased (The is_purchased field of all products is true).
     *
     * @param taskId the unique identifier of the task
     *
     * @return {@code true} if all is_purchased fields of the lists are true;
     * {@code false} otherwise
     */
    boolean isBought(UUID taskId);
}
