package com.github.giga_chill.gigachill.data.access.api;

import com.github.giga_chill.gigachill.model.Event;
import java.util.List;

/**
 * Data Access Object (DAO) interface for managing {@link Event} entities.
 * <p>
 * Provides methods to create, retrieve, update, and delete events,
 * as well as to query user-specific event data.
 * </p>
 */
public interface EventDAO {

     /**
      * Retrieves an event by its unique identifier.
      *
      * @param eventId the unique identifier of the event
      * @return the {@link Event} matching the given ID, or {@code null} if not found
      */
     Event getEventById(String eventId);

     /**
      * Retrieves all events associated with a given user.
      *
      * @param userId the unique identifier of the user
      * @return a {@link List} of {@link Event} objects for the specified user;
      *         empty list if the user has no events
      */
     List<Event> getAllUserEvents(String userId);

     /**
      * Retrieves the role of a user in a specific event.
      *
      * @param userId  the unique identifier of the user
      * @param eventId the unique identifier of the event
      * @return the role name of the user in the event, or {@code null} if none
      */
     String getUserRoleInEvent(String userId, String eventId);

     /**
      * Updates an existing event.
      *
      * @param eventId the unique identifier of the event to update
      * @param event   the {@link Event} object containing updated fields
      * @return the updated {@link Event} instance
      */
     Event updateEvent(String eventId, Event event);

     /**
      * Creates a new event for a user.
      *
      * @param userId the unique identifier of the user creating the event
      * @param event  the {@link Event} object to be persisted
      * @return the newly created {@link Event} with its assigned ID
      */
     Event createEvent(String userId, Event event);

     /**
      * Deletes an event by its unique identifier.
      *
      * @param eventId the unique identifier of the event to delete
      */
     void deleteEvent(String eventId);
}