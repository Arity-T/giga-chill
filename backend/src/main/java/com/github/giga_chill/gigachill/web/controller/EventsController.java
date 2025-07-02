package com.github.giga_chill.gigachill.web.controller;


import com.github.giga_chill.gigachill.exception.UnauthorizedException;
import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.InMemoryUserService;
import com.github.giga_chill.gigachill.web.info.EventInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventsController {

    private final EventService eventService;
    private final InMemoryUserService inMemoryUserService;

    @GetMapping
    public ResponseEntity<List<EventInfo>> getEvents(Authentication authentication){
        var login = authentication.getName();
        if (login == null) {
            throw new UnauthorizedException("Пользователь не найден");
        }
        User user = inMemoryUserService.getByLogin(login);

        var userEvents = eventService.getAllUserEvents(user.id);

        return ResponseEntity.ok(userEvents.stream()
                .map(event -> toEventInfo(event, eventService.getUserRoleInEvent(event.getEvent_id(), user.id)))
                .toList());
    }




    private EventInfo toEventInfo(Event event, String userRole){
        return new EventInfo(event.getEvent_id(), userRole, event.getTitle(), event.getLocation(),
                event.getStart_datetime(), event.getEnd_datetime(), event.getDescription(), event.getBudget());
    }
}
