package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.EventDAO;
import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventDAO eventDAO;

    //TEMPORARY:
//    private final Map<String, Event> EVENTS = new HashMap<>();
//    private final Map<String, Map<String, Event>> USER_EVENTS = new HashMap<>();


    public boolean isExisted(String eventId){
        //TODO: Связь с бд
        return eventDAO.isExisted(eventId);


        //TEMPORARY:
//        return EVENTS.containsKey(eventId);
    }

    public Event getEventById(String eventId) {
        //TODO: Связь с бд
        return toEntity(eventDAO.getEventById(eventId));

        //TEMPORARY:
//        return EVENTS.get(eventId);
    }

    public List<Event> getAllUserEvents(String userId) {
        //TODO: Связь с бд
        return eventDAO.getAllUserEvents(userId).stream()
                .map(this::toEntity).toList();

        //TEMPORARY:
//        if (!USER_EVENTS.containsKey(userId)) {
//            return List.of();
//        }
//
//        return new ArrayList<>(USER_EVENTS.get(userId).values());
    }

    public Event updateEvent(String eventId, RequestEventInfo requestEventInfo) {
        //TODO: Связь с бд
        EventDTO event = new EventDTO(eventId, requestEventInfo.title(),
                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
                requestEventInfo.description(), BigDecimal.valueOf(0));
        eventDAO.updateEvent(eventId, event);
        return getEventById(eventId);


        //TEMPORARY:
//        Event event = EVENTS.get(eventId);
//        Optional.ofNullable(requestEventInfo.title())
//                .ifPresent(event::setTitle);
//        Optional.ofNullable(requestEventInfo.location())
//                .ifPresent(event::setLocation);
//        Optional.ofNullable(requestEventInfo.start_datetime())
//                .ifPresent(event::setStartDatetime);
//        Optional.ofNullable(requestEventInfo.end_datetime())
//                .ifPresent(event::setEndDatetime);
//        Optional.ofNullable(requestEventInfo.description())
//                .ifPresent(event::setDescription);
//
//        return event;
    }


    public Event createEvent(String userId, RequestEventInfo requestEventInfo) {
        Event event = new Event(UUID.randomUUID().toString(), requestEventInfo.title(),
                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
                requestEventInfo.description(), BigDecimal.valueOf(0));
        //TODO: Связь с бд

        eventDAO.createEvent(userId, toDto(event));
        return event;


        //TEMPORARY:
//        EVENTS.put(event.getEventId(), event);
//        USER_EVENTS.computeIfAbsent(userId, value -> new HashMap<>()).put(event.getEventId(), event);
//        return event;
    }

    public void deleteEvent(String eventId) {
        //TODO: Связь с бд
        eventDAO.deleteEvent(eventId);

        //TEMPORARY:
//        EVENTS.remove(eventId);
//        USER_EVENTS.get(userId).remove(eventId);
    }

    private Event toEntity(EventDTO eventDTO){
        return new Event(eventDTO.event_id(),
                eventDTO.title(),
                eventDTO.location(),
                eventDTO.start_datetime(),
                eventDTO.end_datetime(),
                eventDTO.description(),
                eventDTO.budget());
    }

    private EventDTO toDto(Event event){
        return new EventDTO(event.getEventId(),
                event.getTitle(),
                event.getLocation(),
                event.getStartDatetime(),
                event.getEndDatetime(),
                event.getDescription(),
                event.getBudget());
    }

}
