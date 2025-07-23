package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.EventDAO;
import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
import com.github.giga_chill.gigachill.mapper.EventMapper;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import com.github.giga_chill.gigachill.web.info.ResponseEventInfo;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final EventDAO eventDAO;
    private final ParticipantsService participantsService;

    public boolean isExistedAndNotDeleted(UUID eventId) {
        return eventDAO.isExistedAndNotDeleted(eventId);
    }

    public ResponseEventInfo getEventById(UUID userID, UUID eventId) {
        var eventInfo = eventMapper.toInfo(eventDAO.getEventById(eventId));
        eventInfo.setUserRole(
                participantsService.getParticipantRoleInEvent(
                        UuidUtils.safeUUID(eventInfo.getEventId()), userID));

        return eventInfo;
    }

    public List<ResponseEventInfo> getAllUserEvents(UUID userId) {
        return eventDAO.getAllUserEvents(userId).stream()
                .map(eventMapper::toInfo)
                .peek(
                        item ->
                                item.setUserRole(
                                        participantsService.getParticipantRoleInEvent(
                                                UuidUtils.safeUUID(item.getEventId()), userId)))
                .toList();
    }

    public void updateEvent(UUID eventId, RequestEventInfo requestEventInfo) {
        var event =
                new EventDTO(
                        eventId,
                        requestEventInfo.title(),
                        requestEventInfo.location(),
                        requestEventInfo.startDatetime(),
                        requestEventInfo.endDatetime(),
                        requestEventInfo.description(),
                        null,
                        null);
        eventDAO.updateEvent(eventId, event);
    }

    public String createEvent(UUID userId, RequestEventInfo requestEventInfo) {
        var event =
                new EventDTO(
                        UUID.randomUUID(),
                        requestEventInfo.title(),
                        requestEventInfo.location(),
                        requestEventInfo.startDatetime(),
                        requestEventInfo.endDatetime(),
                        requestEventInfo.description(),
                        BigDecimal.valueOf(0),
                        null);

        eventDAO.createEvent(userId, event);
        return event.getEventId().toString();
    }

    public void deleteEvent(UUID eventId) {
        eventDAO.deleteEvent(eventId);
    }

    public String getEndDatetime(UUID eventId) {
        return eventDAO.getEndDatetime(eventId);
    }

    public String createInviteLink(UUID eventId) {
        var inviteLinkUuid = UUID.randomUUID();
        eventDAO.createInviteLink(eventId, inviteLinkUuid);
        return inviteLinkUuid.toString();
    }

    public String getInviteLink(UUID eventId) {
        return eventDAO.getInviteLinkUuid(eventId).toString();
    }

    public UUID getEventByLinkUuid(UUID linkUuid) {
        return eventDAO.getEventByLinkUuid(linkUuid);
    }

    public void joinByLink(UUID eventId, User user) {
        participantsService.addParticipantToEvent(eventId, user);
    }

    public void finalizeEvent(UUID eventId) {
        eventDAO.calculationEventBudget(eventId);
        eventDAO.finalizeEvent(eventId);
    }

    public boolean isFinalized(UUID eventId) {
        return eventDAO.isFinalized(eventId);
    }
}
