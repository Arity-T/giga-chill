package com.github.giga_chill.gigachill.web.controller;


import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.exception.UnauthorizedException;
import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.InMemoryUserService;
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

    @GetMapping
    public ResponseEntity<List<ResponseEventInfo>> getEvents(Authentication authentication){
        User user = userAuthentication(authentication);

        var userEvents = eventService.getAllUserEvents(user.id);

        if (userEvents.isEmpty()){
            ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(userEvents.stream()
                .map(event -> toResponseEventInfo(event, eventService.getUserRoleInEvent(user.id, event.getEvent_id())))
                .toList());
    }

    @PostMapping
    public ResponseEntity<ResponseEventInfo> postEvents(@RequestBody RequestEventInfo requestEventInfo,
                                                        Authentication authentication){

        //TODO: Добавить обработку 400
        User user = userAuthentication(authentication);
        Event event = eventService.createEvent(user.id, requestEventInfo);

        return ResponseEntity.created(URI.create("/events/" + event.getEvent_id()))
                .body(toResponseEventInfo(event, eventService.getUserRoleInEvent(user.id, event.getEvent_id())));
    }

    @GetMapping("/{eventId}")
    //TODO: Настроить подгрузку роли из бд
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_OWNER', ROLE_PARTICIPANT)")
    public ResponseEntity<ResponseEventInfo> getEventById(Authentication authentication, @PathVariable String eventId){
        User user = userAuthentication(authentication);
        Event event = eventService.getEventById(eventId);
        if (event == null){
            throw new NotFoundException("Мероприятие не найдено");
        }
        return ResponseEntity.ok(toResponseEventInfo(event, eventService.getUserRoleInEvent(user.id, event.getEvent_id())));
    }

    @PatchMapping("/{eventId}")
    //TODO: Настроить подгрузку роли из бд
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseEventInfo> patchEventById(@RequestBody RequestEventInfo requestEventInfo,
                                                            Authentication authentication, @PathVariable String eventId){
        //TODO: Добавить обработку 400
        User user = userAuthentication(authentication);
        if (eventService.getEventById(eventId) == null){
            throw new NotFoundException("Мероприятие не найдено");
        }
        Event event = eventService.updateEvent(eventId, requestEventInfo);

        return ResponseEntity.ok(toResponseEventInfo(event, eventService.getUserRoleInEvent(user.id, event.getEvent_id())));
    }

    @PatchMapping("/{eventId}")
    //TODO: Настроить подгрузку роли из бд
//    @PreAuthorize("hasAnyRole('ROLE_OWNER')")
    public ResponseEntity<Void> deleteEventById(@RequestBody RequestEventInfo requestEventInfo,
                                                            Authentication authentication, @PathVariable String eventId){
        //TODO: Добавить обработку 400
        User user = userAuthentication(authentication);
        if (eventService.getEventById(eventId) == null){
            throw new NotFoundException("Мероприятие не найдено");
        }

        eventService.deleteEvent(eventId, user.id);

        return ResponseEntity.noContent().build();
    }


    private User userAuthentication(Authentication authentication){
        var login = authentication.getName();
        if (login == null) {
            throw new UnauthorizedException("Пользователь не найден");
        }
        return inMemoryUserService.getByLogin(login);
    }


    private ResponseEventInfo toResponseEventInfo(Event event, String userRole){
        return new ResponseEventInfo(event.getEvent_id(), userRole, event.getTitle(), event.getLocation(),
                event.getStart_datetime(), event.getEnd_datetime(), event.getDescription(), event.getBudget());
    }

}
