package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.web.api.EventsApi;
import com.github.giga_chill.gigachill.web.api.model.Event;
import com.github.giga_chill.gigachill.web.api.model.EventCreate;
import com.github.giga_chill.gigachill.web.api.model.EventUpdate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

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
