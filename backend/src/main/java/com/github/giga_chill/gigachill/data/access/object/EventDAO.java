package com.github.giga_chill.gigachill.data.access.object;

import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object (DAO) interface for {@link EventDTO} entities.
 *
 * <p>Provides methods to perform CRUD operations and queries related to events and their
 * participants' roles.
 */
public interface EventDAO {

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param eventId the unique identifier of the event
     * @return the {@link EventDTO} matching the given ID, or {@code null} if not found
     */
    EventDTO getEventById(UUID eventId);

    /**
     * Retrieves all events associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a {@link List} of {@link EventDTO} objects for the specified user; an empty list if
     *     the user has no events
     */
    List<EventDTO> getAllUserEvents(UUID userId);

    /**
     * Updates the details of an existing event.
     *
     * @param eventId the unique identifier of the event to update
     * @param event the {@link EventDTO} object containing updated fields; budget is not updated
     */
    void updateEvent(UUID eventId, EventDTO event);

    /**
     * Creates a new event for a given user.
     *
     * @param userId the unique identifier of the user creating the event
     * @param event the {@link EventDTO} object to be persisted
     */
    void createEvent(UUID userId, EventDTO event);

    /**
     * Deletes an event by its unique identifier.
     *
     * @param eventId the unique identifier of the event to delete
     */
    void deleteEvent(UUID eventId);

    /**
     * Checks whether an event with the given identifier exists.
     *
     * @param eventId the unique identifier of the event
     * @return {@code true} if the event exists and delete status false, {@code false} otherwise
     */
    boolean isExistedAndNotDeleted(UUID eventId);

    /**
     * Retrieves the end date and time of the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a {@link String} representation of the event’s end date‑time,
     */
    String getEndDatetime(UUID eventId);

    /**
     * Creates a new invite link record for the specified event.
     *
     * @param eventId the unique identifier of the event
     * @param inviteLinkUuid the UUID to assign as the invite link token
     */
    void createInviteLink(UUID eventId, UUID inviteLinkUuid);

    /**
     * Retrieves the UUID of the current invite link for the given event.
     *
     * @param eventId the unique identifier of the event
     * @return the {@link UUID} representing the invite link token
     */
    UUID getInviteLinkUuid(UUID eventId);

    /**
     * Retrieves the unique Event ID associated with the given invite link UUID.
     *
     * @param linkUuid the UUID token used for event invitation links
     * @return the {@link UUID} of the event linked to the provided invitation token, or {@code
     *     null} if no matching event is found
     */
    @Nullable
    UUID getEventByLinkUuid(UUID linkUuid);

    /**
     * Calculates and updates the overall budget for the specified event. This may involve
     * aggregating individual shopping list budgets and other cost components, then persisting the
     * updated total.
     *
     * @param eventId the unique identifier of the event to recalculate the budget for
     */
    void calculationEventBudget(UUID eventId);

    /**
     * Marks the specified event as finalized, preventing further modifications. Executes any
     * finalization logic before setting the event’s status to closed.
     *
     * @param eventId the unique identifier of the event to close
     */
    void finalizeEvent(UUID eventId);

    /**
     * Determines whether the specified event has been finalized (closed).
     *
     * @param eventId the unique identifier of the event to check
     * @return {@code true} if the event is finalized and no longer editable; {@code false}
     *     otherwise
     */
    boolean isFinalized(UUID eventId);
}
