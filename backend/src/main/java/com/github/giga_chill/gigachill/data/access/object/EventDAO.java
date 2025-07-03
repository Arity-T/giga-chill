package com.github.giga_chill.gigachill.data.access.object;

import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
import java.util.List;

/**
 * Data Access Object (DAO) interface for {@link EventDTO} entities.
 * <p>
 * Provides methods to perform CRUD operations and queries related to events
 * and their participants' roles.
 * </p>
 */
public interface EventDAO {

     /**
      * Retrieves an event by its unique identifier.
      *
      * @param eventId the unique identifier of the event
      * @return the {@link EventDTO} matching the given ID, or {@code null} if not found
      */
     EventDTO getEventById(String eventId);

     /**
      * Retrieves all events associated with a specific user.
      *
      * @param userId the unique identifier of the user
      * @return a {@link List} of {@link EventDTO} objects for the specified user;
      *         an empty list if the user has no events
      */
     List<EventDTO> getAllUserEvents(String userId);

     /**
      * Retrieves the role of a user in a specific event.
      *
      * @param userId  the unique identifier of the user
      * @param eventId the unique identifier of the event
      * @return the role name of the user in the event, or {@code null} if none
      */
     String getUserRoleInEvent(String userId, String eventId);

     /**
      * Updates the details of an existing event.
      *
      * @param eventId the unique identifier of the event to update
      * @param event   the {@link EventDTO} object containing updated fields
      * @return the updated {@link EventDTO} instance
      */
     EventDTO updateEvent(String eventId, EventDTO event);

     /**
      * Creates a new event for a given user.
      *
      * @param userId the unique identifier of the user creating the event
      * @param event  the {@link EventDTO} object to be persisted
      * @return the newly created {@link EventDTO} with its assigned ID
      */
     EventDTO createEvent(String userId, EventDTO event);

     /**
      * Deletes an event by its unique identifier.
      *
      * @param eventId the unique identifier of the event to delete
      */
     void deleteEvent(String eventId);

     /**
      * Checks whether an event with the given identifier exists.
      *
      * @param eventId the unique identifier of the event
      * @return {@code true} if the event exists, {@code false} otherwise
      */
     boolean isExists(String eventId);

}
