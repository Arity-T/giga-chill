package com.github.giga_chill.gigachill.data.access.object;

import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
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
     * @param event the {@link EventDTO} object containing updated fields
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
     * @return {@code true} if the event exists, {@code false} otherwise
     */
    boolean isExisted(UUID eventId);


    /**
     * Retrieves the end date and time of the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a {@link String} representation of the event’s end date‑time,
     */
    String getEndDatetime(UUID eventId);
}
