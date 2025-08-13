package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.EventDAO;
import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.mapper.EventMapper;
import com.github.giga_chill.gigachill.model.UserEntity;
import com.github.giga_chill.gigachill.service.validator.EventServiceValidator;
import com.github.giga_chill.gigachill.service.validator.ParticipantServiceValidator;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.api.model.Event;
import com.github.giga_chill.gigachill.web.api.model.EventCreate;
import com.github.giga_chill.gigachill.web.api.model.EventUpdate;
import com.github.giga_chill.gigachill.web.api.model.UserRole;
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
    private final ParticipantServiceValidator participantsServiceValidator;

    public Event getEventById(UUID userId, UUID eventId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);

        var event = eventMapper.toEvent(eventDAO.getEventById(eventId));
        event.setUserRole(
                UserRole.fromValue(
                        participantsService.getParticipantRoleInEvent(event.getEventId(), userId)));

        return event;
    }

    public List<Event> getAllUserEvents(UUID userId) {
        return eventDAO.getAllUserEvents(userId).stream()
                .map(eventMapper::toEvent)
                .peek(
                        item ->
                                item.setUserRole(
                                        UserRole.fromValue(
                                                participantsService.getParticipantRoleInEvent(
                                                        item.getEventId(), userId))))
                .toList();
    }

    public void updateEvent(UUID eventId, UUID userId, EventUpdate eventUpdate) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkAdminOrOwnerRole(eventId, userId);

        var event =
                new EventDTO(
                        eventId,
                        eventUpdate.getTitle(),
                        eventUpdate.getLocation(),
                        eventUpdate.getStartDatetime(),
                        eventUpdate.getEndDatetime(),
                        eventUpdate.getDescription(),
                        null,
                        null);
        eventDAO.updateEvent(eventId, event);
    }

    public String createEvent(UUID userId, EventCreate eventCreate) {
        var event =
                new EventDTO(
                        UUID.randomUUID(),
                        eventCreate.getTitle(),
                        eventCreate.getLocation(),
                        eventCreate.getStartDatetime(),
                        eventCreate.getEndDatetime(),
                        eventCreate.getDescription(),
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

    public UUID joinByLink(UserEntity userEntity, Map<String, Object> body) {
        var rawToken = (String) body.get("invitation_token");
        if (Objects.isNull(rawToken)) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        var eventId = getEventByLinkUuid(UuidUtils.safeUUID(rawToken));
        if (Objects.isNull(eventId)) {
            throw new NotFoundException("Link with hash " + rawToken + " not found");
        }
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsAlreadyParticipant(eventId, userEntity.getId());

        participantsService.addParticipantToEvent(eventId, userEntity);
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
