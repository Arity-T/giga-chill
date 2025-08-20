package ru.gigachill.repository.composite;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import ru.gigachill.data.transfer.object.TaskDTO;
import ru.gigachill.data.transfer.object.TaskWithShoppingListsDTO;

/**
 * Data Access Object (DAO) interface for managing tasks within events.
 *
 * <p>Provides methods to perform CRUD operations, execution control, and permission checks on
 * tasks.
 */
public interface TaskCompositeRepository {

    /**
     * Retrieves all tasks associated with the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a {@link List} of {@link TaskDTO} representing all tasks in the event; empty list if
     *     none found
     */
    List<TaskDTO> getAllTasksFromEvent(UUID eventId);

    /**
     * Retrieves a task by its unique identifier, including its associated shopping lists.
     *
     * @param taskId the unique identifier of the task
     * @return a {@link TaskWithShoppingListsDTO} containing task details and its shopping list
     *     references
     */
    TaskWithShoppingListsDTO getTaskById(UUID taskId);

    /**
     * Creates a new task for the specified event and associates it with the given shopping lists.
     *
     * @param eventId the unique identifier of the event in which to create the task
     * @param taskDTO the {@link TaskDTO} containing details of the task to create
     * @param shoppingListsIds a {@link List} of {@link UUID} values representing shopping lists to
     *     attach to the task
     */
    void createTask(UUID eventId, TaskDTO taskDTO, List<UUID> shoppingListsIds);

    /**
     * Updates an existing task’s data.
     *
     * @param taskId the unique identifier of the task to update
     * @param taskDTO the {@link TaskDTO} containing the updated task information
     */
    void updateTask(UUID taskId, TaskDTO taskDTO);

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
     * @param taskId the unique identifier of the task
     * @return {@code true} if the task exists within the event; {@code false} otherwise
     */
    boolean isExisted(UUID eventId, UUID taskId);

    /**
     * Determines if a user has permission to execute the specified task.
     *
     * @param taskId the unique identifier of the task
     * @param userId the unique identifier of the user
     * @return {@code true} if the user can execute the task(if he is executor or executor is null);
     *     {@code false} otherwise
     */
    boolean canExecute(UUID taskId, UUID userId);

    /**
     * Retrieves the identifier of the user who is currently executing the specified task.
     *
     * @param taskId the unique identifier of the task
     * @return the {@link UUID} of the executor user if one is assigned; {@code null} if the task
     *     has not been started or no executor is set
     */
    @Nullable
    UUID getExecutorId(UUID taskId);

    /**
     * Assigns or unassigns the executor for the specified task.
     *
     * @param taskId the unique identifier of the task to update
     * @param executorId the {@link UUID} of the user who will execute the task; may be {@code null}
     *     to clear any existing executor
     */
    void updateExecutor(UUID taskId, @Nullable UUID executorId);

    /**
     * Updates the set of shopping lists associated with the specified task.
     *
     * @param taskId the unique identifier of the task to update
     * @param shoppingLists a {@link List} of {@link UUID} values representing new shopping list IDs
     *     to bind; may be {@code null} to clear all associations If the list is unlinked, its
     *     status becomes "Unassigned". And all the purchases become not purchased.
     */
    void updateShoppingLists(UUID taskId, @Nullable List<UUID> shoppingLists);

    /**
     * Updates the comment provided by the executor for the specified task.
     *
     * @param taskId the unique identifier of the task to update
     * @param executorComment the comment text from the executor
     */
    void setExecutorComment(UUID taskId, String executorComment);

    /**
     * Updates the reviewer's comment and approval status for the specified task.
     *
     * @param taskId the unique identifier of the task to update
     * @param reviewerComment the comment text from the reviewer; may be empty or null to clear
     *     existing comment
     * @param isApproved {@code true} if the reviewer approves the task - The task status becomes
     *     "completed"; {@code false} otherwise - The task status becomes "in_progress"
     */
    void setReviewerComment(UUID taskId, String reviewerComment, boolean isApproved);
}
