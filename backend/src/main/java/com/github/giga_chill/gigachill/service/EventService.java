package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.EventDAO;
import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
@RequiredArgsConstructor
public class EventService {

    private final Environment env;
    private final EventDAO eventDAO;
    private final ParticipantsService participantsService;


    public boolean isExisted(UUID eventId) {
        return eventDAO.isExisted(eventId);
    }

    public Event getEventById(UUID eventId) {
        return toEntity(eventDAO.getEventById(eventId));
    }

    public List<Event> getAllUserEvents(UUID userId) {
        return eventDAO.getAllUserEvents(userId).stream()
                .map(this::toEntity).toList();

    }

    public void updateEvent(UUID eventId, RequestEventInfo requestEventInfo) {
        EventDTO event = new EventDTO(eventId, requestEventInfo.title(),
                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
                requestEventInfo.description(), BigDecimal.valueOf(0));
        eventDAO.updateEvent(eventId, event);
    }


    public String createEvent(UUID userId, RequestEventInfo requestEventInfo) {
        Event event = new Event(UUID.randomUUID(), requestEventInfo.title(),
                requestEventInfo.location(), requestEventInfo.start_datetime(), requestEventInfo.end_datetime(),
                requestEventInfo.description(), BigDecimal.valueOf(0));

        eventDAO.createEvent(userId, toDto(event));
        return event.getEventId().toString();
    }

    public void deleteEvent(UUID eventId) {
        eventDAO.deleteEvent(eventId);
    }

    public String createInviteLink(UUID eventId){
        var inviteLinkUuid = UUID.randomUUID();
        eventDAO.createInviteLink(eventId, inviteLinkUuid);
        return inviteLinkUuid.toString();
    }

    public String getInviteLink(UUID eventId){
        var inviteLingUuid = eventDAO.getInviteLinkUuid(eventId);
        if (inviteLingUuid == null){
            return null;
        }
        return env.getProperty("frontend.protocol") + "://" + env.getProperty("frontend.address")
                + "/events/" + eventId.toString() + "/join-by-link/" + inviteLingUuid.toString();
    }

    public boolean isCorrectLinkUuid(UUID eventId, UUID linkUuid){
        return eventDAO.isCorrectLinkUuid(eventId, linkUuid);
    }

    public void joinByLink(UUID eventId, User user){
        participantsService.addParticipantToEvent(eventId, user);
    }

    private Event toEntity(EventDTO eventDTO) {
        return new Event(eventDTO.event_id(),
                eventDTO.title(),
                eventDTO.location(),
                eventDTO.start_datetime(),
                eventDTO.end_datetime(),
                eventDTO.description(),
                eventDTO.budget());
    }

    private EventDTO toDto(Event event) {
        return new EventDTO(event.getEventId(),
                event.getTitle(),
                event.getLocation(),
                event.getStartDatetime(),
                event.getEndDatetime(),
                event.getDescription(),
                event.getBudget());
    }

}
