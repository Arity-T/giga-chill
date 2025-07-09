package com.github.giga_chill.gigachill.web.controller;


import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.service.ParticipantsService;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import com.github.giga_chill.gigachill.web.info.ResponseEventInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventsController {

    private final EventService eventService;
    private final UserService userService;
    private final ParticipantsService participantsService;

    @GetMapping
    //ACCESS: ALL
    public ResponseEntity<List<ResponseEventInfo>> getEvents(Authentication authentication){
        User user = userService.userAuthentication(authentication);

        var userEvents = eventService.getAllUserEvents(user.id);

        if (userEvents.isEmpty()){
            ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(userEvents.stream()
                .map(event -> toResponseEventInfo(event,
                        participantsService.getParticipantRoleInEvent(event.getEventId(), user.id)))
                .toList());
    }

    @PostMapping
    //ACCESS: ALL
    public ResponseEntity<ResponseEventInfo> postEvents(@RequestBody RequestEventInfo requestEventInfo,
                                                        Authentication authentication){

        User user = userService.userAuthentication(authentication);
        Event event = eventService.createEvent(user.id, requestEventInfo);
        return ResponseEntity.created(URI.create("/events/" + event.getEventId()))
                .body(toResponseEventInfo(event,
                        participantsService.getParticipantRoleInEvent(event.getEventId(), user.id)));
    }

    @GetMapping("/{eventId}")
    //ACCESS: owner, admin, participant
    public ResponseEntity<ResponseEventInfo> getEventById(Authentication authentication, @PathVariable String eventId){
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)){
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if(!participantsService.isParticipant(eventId, user.id)){
            throw new ForbiddenException("User with id " + user.id +
                    " is not a participant of event with id " + eventId);
        }
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(toResponseEventInfo(event, participantsService.getParticipantRoleInEvent(event.getEventId(), user.id)));
    }

    @PatchMapping("/{eventId}")
    //ACCESS: owner, admin
    public ResponseEntity<ResponseEventInfo> patchEventById(@RequestBody RequestEventInfo requestEventInfo,
                                                            Authentication authentication, @PathVariable String eventId){
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)){
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.id)){
            throw new ForbiddenException("User with id " + user.id +
                    " is not a participant of event with id " + eventId);
        }
        if (!participantsService.isOwnerRole(eventId, user.id) && !participantsService.isAdminRole(eventId, user.id)){
            throw new ForbiddenException("User with id " + user.id +
                    " does not have permission to patch event with id " + eventId);
        }
        Event event = eventService.updateEvent(eventId, requestEventInfo);

        return ResponseEntity.ok(toResponseEventInfo(event,
                participantsService.getParticipantRoleInEvent(event.getEventId(), user.id)));
    }

    @DeleteMapping("/{eventId}")
    //ACCESS: owner
    public ResponseEntity<Void> deleteEventById(Authentication authentication, @PathVariable String eventId){
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)){
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.id)){
            throw new ForbiddenException("User with id " + user.id +
                    " is not a participant of event with id " + eventId);
        }
        if (!participantsService.isOwnerRole(eventId, user.id)){
            throw new ForbiddenException("User with id " + user.id +
                    " does not have permission to delete event with id " + eventId);
        }
        eventService.deleteEvent(eventId);

        return ResponseEntity.noContent().build();
    }

    private ResponseEventInfo toResponseEventInfo(Event event, String userRole){
        return new ResponseEventInfo(event.getEventId(), userRole, event.getTitle(), event.getLocation(),
                event.getStartDatetime(), event.getEndDatetime(), event.getDescription(), event.getBudget());
    }

}
