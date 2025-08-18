package ru.gigachill.web.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.gigachill.service.EventService;
import ru.gigachill.service.UserService;
import ru.gigachill.web.api.EventsApi;
import ru.gigachill.web.api.model.Event;
import ru.gigachill.web.api.model.EventCreate;
import ru.gigachill.web.api.model.EventUpdate;

@RequiredArgsConstructor
@RestController
public class EventsController implements EventsApi {
    private final EventService eventService;
    private final UserService userService;

    @Override
    // ACCESS: ALL
    public ResponseEntity<Void> createEvent(EventCreate eventCreate) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        eventService.createEvent(user.getId(), eventCreate);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner
    public ResponseEntity<Void> deleteEvent(UUID eventId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        eventService.deleteEvent(eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant
    public ResponseEntity<Event> getEvent(UUID eventId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(eventService.getEventById(user.getId(), eventId));
    }

    @Override
    // ACCESS: ALL
    public ResponseEntity<List<Event>> getEvents() {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        var userEvents = eventService.getAllUserEvents(user.getId());

        return ResponseEntity.ok(userEvents.isEmpty() ? null : userEvents);
    }

    @Override
    // ACCESS: owner, admin
    public ResponseEntity<Void> updateEvent(UUID eventId, EventUpdate eventUpdate) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());

        eventService.updateEvent(eventId, user.getId(), eventUpdate);
        return ResponseEntity.noContent().build();
    }
}
