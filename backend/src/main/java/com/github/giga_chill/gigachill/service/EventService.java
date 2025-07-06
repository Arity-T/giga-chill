package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.EventDAO;
import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class EventService {

    private EventDAO eventDAO;

    //TEMPORARY:
    private final Map<String, Event> EVENTS = new HashMap<>();
    private final Map<String, Map<String, Event>> USER_EVENTS = new HashMap<>();


    public boolean isExisted(String eventId){
        //TODO: Связь с бд
        //return eventDAO.isExisted(eventId);


        //TEMPORARY:
        return EVENTS.containsKey(eventId);
    }

    public Event getEventById(String eventId) {
        //TODO: Связь с бд
//        return eventDAO.getEventById(eventId)

        //TEMPORARY:
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

    public Event updateEvent(String eventId, RequestEventInfo requestEventInfo) {
        //TODO: Связь с бд
//        Event event = new Event(eventId, requestEventInfo.title(),
//                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
//                requestEventInfo.description(), 0);
//
//        return eventDAO.updateEvent(eventId, event);


        //TEMPORARY:
        Event event = EVENTS.get(eventId);
        Optional.ofNullable(requestEventInfo.title())
                .ifPresent(event::setTitle);
        Optional.ofNullable(requestEventInfo.location())
                .ifPresent(event::setLocation);
        Optional.ofNullable(requestEventInfo.start_datetime())
                .ifPresent(event::setStartDatetime);
        Optional.ofNullable(requestEventInfo.end_datetime())
                .ifPresent(event::setEndDatetime);
        Optional.ofNullable(requestEventInfo.description())
                .ifPresent(event::setDescription);

        return event;
    }


    public Event createEvent(String userId, RequestEventInfo requestEventInfo) {
        Event event = new Event(UUID.randomUUID().toString(), requestEventInfo.title(),
                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
                requestEventInfo.description(), 0);
        //TODO: Связь с бд
//        return eventDAO.createEvent(userId, event);


        //TEMPORARY:
        EVENTS.put(event.getEventId(), event);
        USER_EVENTS.computeIfAbsent(userId, value -> new HashMap<>()).put(event.getEventId(), event);
        return event;
    }

    public void deleteEvent(String eventId, String userId) {
        //TODO: Связь с бд
        //eventDAO.deleteEvent(eventId);

        //TEMPORARY:
        EVENTS.remove(eventId);
        USER_EVENTS.get(userId).remove(eventId);
    }
}
