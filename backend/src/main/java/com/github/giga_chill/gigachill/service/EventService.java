package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.api.EventDAO;
import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.model.Role;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class EventService {

    private EventDAO eventDAO;

    //TEMPORARY:
    private final Map<String, Event> EVENTS = new HashMap<>();
    private final Map<String, Map<String, Event>> USER_EVENTS = new HashMap<>();
    private final Map<String, Map<String, String>> USER_EVENT_ROLES = new HashMap<>();


    public Event getEventById(String eventId) {
        //TODO: Связь с бд
//        return eventDAO.getEventById(eventId)


        return EVENTS.get(eventId);
    }

    public List<Event> getAllUserEvents(String userId) {
        //TODO: Связь с бд
//        return eventDAO.getAllUserEvents(userId);

        //TEMPORARY:
        if (!USER_EVENTS.containsKey(userId)) {
            return List.of();
        }

        return new ArrayList<>(USER_EVENTS.get(userId).values());
    }

    public String getUserRoleInEvent(String userId, String eventId) {
        //TODO: Связь с бд
//        return eventDAO.getUserRoleInEvent(userId, eventId);

        return USER_EVENT_ROLES.get(userId).get(eventId);
    }

    public Event updateEvent(String eventId, RequestEventInfo requestEventInfo) {
        //TODO: Связь с бд
//        Event event = new Event(eventId, requestEventInfo.title(),
//                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
//                requestEventInfo.description(), 0);
//
//        return eventDAO.updateEvent(eventId, event);


        //TEMPORARY:
        Event event = EVENTS.get(eventId);
        event.setTitle(requestEventInfo.title());
        event.setLocation(requestEventInfo.location());
        event.setStart_datetime(requestEventInfo.start_datetime());
        event.setEnd_datetime(requestEventInfo.end_datetime());
        event.setDescription(requestEventInfo.description());


        return event;
    }


    public Event createEvent(String userId, RequestEventInfo requestEventInfo) {
        Event event = new Event(UUID.randomUUID().toString(), requestEventInfo.title(),
                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
                requestEventInfo.description(), 0);
        //TODO: Связь с бд
//        return eventDAO.createEvent(userId, event);


        //TEMPORARY:
        EVENTS.put(event.getEvent_id(), event);
        USER_EVENTS.computeIfAbsent(userId, value -> new HashMap<>()).put(event.getEvent_id(), event);
        USER_EVENT_ROLES.computeIfAbsent(userId, value -> new HashMap<>()).put(event.getEvent_id(), Role.ROLE_OWNER.toString());
        return event;
    }

    public void deleteEvent(String eventId, String userId) {
        //TODO: Связь с бд
        //eventDAO.deleteEvent(eventId);

        //TEMPORARY:
        EVENTS.remove(eventId);
        USER_EVENTS.get(userId).remove(eventId);
        USER_EVENT_ROLES.get(userId).remove(eventId);
    }
}
