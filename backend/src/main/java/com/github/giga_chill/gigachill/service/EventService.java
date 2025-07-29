package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.EventDAO;
import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.mapper.EventMapper;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.validator.EventServiceValidator;
import com.github.giga_chill.gigachill.service.validator.ParticipantsServiceValidator;
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
    private final ParticipantService participantsService;
    private final EventServiceValidator eventServiceValidator;
    private final ParticipantsServiceValidator participantsServiceValidator;

    public ResponseEventInfo getEventById(UUID userId, UUID eventId) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        var eventInfo = eventMapper.toResponseEventInfo(eventDAO.getEventById(eventId));
        eventInfo.setUserRole(
                participantsService.getParticipantRoleInEvent(
                        UuidUtils.safeUUID(eventInfo.getEventId()), userId));

        return eventInfo;
    }

    public List<ResponseEventInfo> getAllUserEvents(UUID userId) {
        return eventDAO.getAllUserEvents(userId).stream()
                .map(eventMapper::toResponseEventInfo)
                .peek(
                        item ->
                                item.setUserRole(
                                        participantsService.getParticipantRoleInEvent(
                                                UuidUtils.safeUUID(item.getEventId()), userId)))
                .toList();
    }

    public void updateEvent(UUID eventId, UUID userId, RequestEventInfo requestEventInfo) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkAdminOrOwnerRole(eventId, userId);

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

    public void deleteEvent(UUID eventId, UUID userId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkOwnerRole(eventId, userId);

        eventDAO.deleteEvent(eventId);
    }

    public String getEndDatetime(UUID eventId) {
        return eventDAO.getEndDatetime(eventId);
    }

    public String createInviteLink(UUID eventId, UUID userId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkOwnerRole(eventId, userId);

        var inviteLinkUuid = UUID.randomUUID();
        eventDAO.createInviteLink(eventId, inviteLinkUuid);
        return inviteLinkUuid.toString();
    }

    public String getInviteLink(UUID eventId, UUID userId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkAdminOrOwnerRole(eventId, userId);

        return eventDAO.getInviteLinkUuid(eventId).toString();
    }

    public UUID getEventByLinkUuid(UUID linkUuid) {
        return eventDAO.getEventByLinkUuid(linkUuid);
    }

    public UUID joinByLink(User user, Map<String, Object> body) {
        var rawToken = (String) body.get("invitation_token");
        if (rawToken == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        var eventId = getEventByLinkUuid(UuidUtils.safeUUID(rawToken));
        if (eventId == null) {
            throw new NotFoundException("Link with hash " + rawToken + " not found");
        }
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsAlreadyParticipant(eventId, user.getId());

        participantsService.addParticipantToEvent(eventId, user);
        return eventId;
    }

    public void finalizeEvent(UUID eventId, UUID userId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkOwnerRole(eventId, userId);

        eventDAO.calculationEventBudget(eventId);
        eventDAO.finalizeEvent(eventId);
    }
}
