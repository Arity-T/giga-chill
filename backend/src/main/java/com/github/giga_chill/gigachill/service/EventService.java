package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.model.Role;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventService {

    //Временно
    private final Map<String, Event> EVENTS = new HashMap<>();
    private final Map<String, List<Event>> USER_EVENTS = new HashMap<>();
    public final Map<String, Map<String, String>> USER_EVENT_ROLES = new HashMap<>();


    public List<Event> getAllUserEvents(String userId){
        //TODO: Связь с бд
        return USER_EVENTS.get(userId);
    }

    public String getUserRoleInEvent(String userId, String eventId){
        //TODO: Связь с бд
        return USER_EVENT_ROLES.get(userId).get(eventId);
    }

    public Event createEvent(String userId, RequestEventInfo requestEventInfo){
        Event event = new Event(UUID.randomUUID().toString(), requestEventInfo.title(),
                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
                requestEventInfo.description(), 0);
        //TODO: Связь с бд

        //Временно
        EVENTS.put(event.getEvent_id(), event);
        USER_EVENTS.computeIfAbsent(userId, value -> new ArrayList<>()).add(event);
        USER_EVENT_ROLES.computeIfAbsent(userId, value -> new HashMap<>()).put(event.getEvent_id(), Role.ROLE_OWNER.toString());
        return event;
    }
}
