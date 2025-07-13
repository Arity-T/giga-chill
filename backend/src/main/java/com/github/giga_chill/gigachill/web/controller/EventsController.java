package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.ParticipantsService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.util.InfoEntityMapper;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import com.github.giga_chill.gigachill.web.info.ResponseEventInfo;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventsController {

    private final EventService eventService;
    private final UserService userService;
    private final ParticipantsService participantsService;

    @GetMapping
    // ACCESS: ALL
    public ResponseEntity<List<ResponseEventInfo>> getEvents(Authentication authentication) {
        User user = userService.userAuthentication(authentication);

        var userEvents = eventService.getAllUserEvents(user.getId());

        if (userEvents.isEmpty()) {
            ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(
                userEvents.stream()
                        .map(
                                event ->
                                        InfoEntityMapper.toResponseEventInfo(
                                                event,
                                                participantsService.getParticipantRoleInEvent(
                                                        event.getEventId(), user.getId())))
                        .toList());
    }

    @PostMapping
    // ACCESS: ALL
    public ResponseEntity<Void> postEvents(
            @RequestBody RequestEventInfo requestEventInfo, Authentication authentication) {

        User user = userService.userAuthentication(authentication);
        eventService.createEvent(user.getId(), requestEventInfo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}")
    // ACCESS: owner, admin, participant
    public ResponseEntity<ResponseEventInfo> getEventById(
            Authentication authentication, @PathVariable UUID eventId) {
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(
                InfoEntityMapper.toResponseEventInfo(
                        event,
                        participantsService.getParticipantRoleInEvent(
                                event.getEventId(), user.getId())));
    }

    @PatchMapping("/{eventId}")
    // ACCESS: owner, admin
    public ResponseEntity<Void> patchEventById(
            @RequestBody RequestEventInfo requestEventInfo,
            Authentication authentication,
            @PathVariable UUID eventId) {
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (!participantsService.isOwnerRole(eventId, user.getId())
                && !participantsService.isAdminRole(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " does not have permission to patch event with id "
                            + eventId);
        }
        eventService.updateEvent(eventId, requestEventInfo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}")
    // ACCESS: owner
    public ResponseEntity<Void> deleteEventById(
            Authentication authentication, @PathVariable UUID eventId) {
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (!participantsService.isOwnerRole(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " does not have permission to delete event with id "
                            + eventId);
        }
        eventService.deleteEvent(eventId);

        return ResponseEntity.noContent().build();
    }
}
