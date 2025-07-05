package com.github.giga_chill.gigachill.web.controller;


import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.InMemoryUserService;
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
    private final InMemoryUserService inMemoryUserService;
    private final ParticipantsService participantsService;

    @GetMapping
    //ACCESS: ALL
    public ResponseEntity<List<ResponseEventInfo>> getEvents(Authentication authentication){
        User user = inMemoryUserService.userAuthentication(authentication);

        var userEvents = eventService.getAllUserEvents(user.id);

        if (userEvents.isEmpty()){
            ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(userEvents.stream()
                .map(event -> toResponseEventInfo(event,
                        participantsService.getParticipantRoleInEvent(event.getEvent_id(), user.id)))
                .toList());
    }

    @PostMapping
    //ACCESS: ALL
    public ResponseEntity<ResponseEventInfo> postEvents(@RequestBody RequestEventInfo requestEventInfo,
                                                        Authentication authentication){

        User user = inMemoryUserService.userAuthentication(authentication);
        Event event = eventService.createEvent(user.id, requestEventInfo);
        participantsService.createEvent(event.getEvent_id(), user);
        return ResponseEntity.created(URI.create("/events/" + event.getEvent_id()))
                .body(toResponseEventInfo(event,
                        participantsService.getParticipantRoleInEvent(event.getEvent_id(), user.id)));
    }

    @GetMapping("/{eventId}")
    //ACCESS: owner, admin, participant
    public ResponseEntity<ResponseEventInfo> getEventById(Authentication authentication, @PathVariable String eventId){
        User user = inMemoryUserService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)){
            throw new NotFoundException("Мероприятие не найдено");
        }
        if(!participantsService.IsParticipant(eventId, user.id)){
            throw new ForbiddenException("Пользователь не является участником мероприятия");
        }
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(toResponseEventInfo(event, participantsService.getParticipantRoleInEvent(event.getEvent_id(), user.id)));
    }

    @PatchMapping("/{eventId}")
    //ACCESS: owner, admin
    public ResponseEntity<ResponseEventInfo> patchEventById(@RequestBody RequestEventInfo requestEventInfo,
                                                            Authentication authentication, @PathVariable String eventId){
        User user = inMemoryUserService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)){
            throw new NotFoundException("Мероприятие не найдено");
        }
        if (!participantsService.IsParticipant(eventId, user.id)){
            throw new ForbiddenException("Пользователь не является участником мероприятия");
        }
        if (!participantsService.isOwner(eventId, user.id) && !participantsService.isAdmin(eventId, user.id)){
            throw new ForbiddenException("Недостаточно прав");
        }
        Event event = eventService.updateEvent(eventId, requestEventInfo);

        return ResponseEntity.ok(toResponseEventInfo(event,
                participantsService.getParticipantRoleInEvent(event.getEvent_id(), user.id)));
    }

    @DeleteMapping("/{eventId}")
    //ACCESS: owner
    public ResponseEntity<Void> deleteEventById(Authentication authentication, @PathVariable String eventId){
        User user = inMemoryUserService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)){
            throw new NotFoundException("Мероприятие не найдено");
        }
        if (!participantsService.IsParticipant(eventId, user.id)){
            throw new ForbiddenException("Пользователь не является участником мероприятия");
        }
        if (!participantsService.isOwner(eventId, user.id)){
            throw new ForbiddenException("Недостаточно прав");
        }
        eventService.deleteEvent(eventId, user.id);

        return ResponseEntity.noContent().build();
    }

    private ResponseEventInfo toResponseEventInfo(Event event, String userRole){
        return new ResponseEventInfo(event.getEvent_id(), userRole, event.getTitle(), event.getLocation(),
                event.getStart_datetime(), event.getEnd_datetime(), event.getDescription(), event.getBudget());
    }

}
