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


    public boolean isExisted(String eventId){
        return eventDAO.isExisted(eventId);
    }

    public Event getEventById(String eventId) {
        return toEntity(eventDAO.getEventById(eventId));
    }

    public List<Event> getAllUserEvents(String userId) {
        return eventDAO.getAllUserEvents(userId).stream()
                .map(this::toEntity).toList();

    }

    public Event updateEvent(String eventId, RequestEventInfo requestEventInfo) {
        EventDTO event = new EventDTO(eventId, requestEventInfo.title(),
                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
                requestEventInfo.description(), BigDecimal.valueOf(0));
        eventDAO.updateEvent(eventId, event);
        return getEventById(eventId);

    }


    public Event createEvent(String userId, RequestEventInfo requestEventInfo) {
        Event event = new Event(UUID.randomUUID().toString(), requestEventInfo.title(),
                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
                requestEventInfo.description(), BigDecimal.valueOf(0));

        eventDAO.createEvent(userId, toDto(event));
        return event;
    }

    public void deleteEvent(String eventId) {
        eventDAO.deleteEvent(eventId);
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
