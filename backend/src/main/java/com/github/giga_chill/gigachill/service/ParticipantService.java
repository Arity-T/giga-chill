package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.ParticipantDAO;
import com.github.giga_chill.gigachill.data.transfer.object.ParticipantDTO;
import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.mapper.ParticipantBalanceMapper;
import com.github.giga_chill.gigachill.mapper.ParticipantMapper;
import com.github.giga_chill.gigachill.mapper.ParticipantSummaryBalanceMapper;
import com.github.giga_chill.gigachill.model.UserEntity;
import com.github.giga_chill.gigachill.service.validator.EventServiceValidator;
import com.github.giga_chill.gigachill.service.validator.ParticipantServiceValidator;
import com.github.giga_chill.gigachill.web.api.model.ParticipantBalanceSummary;
import com.github.giga_chill.gigachill.web.api.model.UserBalance;
import com.github.giga_chill.gigachill.web.info.ParticipantInfo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantBalanceMapper participantBalanceMapper;
    private final ParticipantSummaryBalanceMapper participantSummaryBalanceMapper;
    private final ParticipantMapper participantMapper;
    private final Environment env;
    private final ParticipantDAO participantDAO;
    private final EventServiceValidator eventServiceValidator;
    private final ParticipantServiceValidator participantsServiceValidator;
    private final UserService userService;

    public List<ParticipantInfo> getAllParticipantsByEventId(UUID eventId, UUID participantId) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, participantId);

        return participantDAO.getAllParticipantsByEventId(eventId).stream()
                .map(participantMapper::toParticipantInfo)
                .toList();
    }

    public ParticipantInfo getParticipantById(UUID eventId, UUID participantId) {
        return participantMapper.toParticipantInfo(
                participantDAO.getParticipantById(eventId, participantId));
    }

    public UUID addParticipantToEvent(UUID eventId, UUID participantId, Map<String, Object> body) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, participantId);
        participantsServiceValidator.checkAdminOrOwnerRole(eventId, participantId);

        var participantLogin = (String) body.get("login");
        if (Objects.isNull(participantLogin)) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        var userToAdd = userService.getByLogin(participantLogin);

        if (Objects.isNull(userToAdd)) {
            throw new NotFoundException("User with login '" + participantLogin + "' not found");
        }

        participantsServiceValidator.checkIsAlreadyParticipant(eventId, userToAdd.getId());

        var participant =
                new ParticipantDTO(
                        userToAdd.getId(),
                        userToAdd.getLogin(),
                        userToAdd.getName(),
                        env.getProperty("roles.participant").toString(),
                        BigDecimal.valueOf(0));

        participantDAO.addParticipantToEvent(eventId, participant);
        return userToAdd.getId();
    }

    public UUID addParticipantToEvent(UUID eventId, UserEntity userEntity) {
        var participant =
                new ParticipantDTO(
                        userEntity.getId(),
                        userEntity.getLogin(),
                        userEntity.getName(),
                        env.getProperty("roles.participant").toString(),
                        BigDecimal.valueOf(0));

        participantDAO.addParticipantToEvent(eventId, participant);
        return userEntity.getId();
    }

    public void deleteParticipant(UUID eventId, UUID participantId, UUID userId) {

        participantsServiceValidator.checkIsSamePerson(userId, participantId);
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkAdminOrOwnerRole(eventId, userId);
        participantsServiceValidator.checkIsParticipant(eventId, participantId);

        participantDAO.deleteParticipant(eventId, participantId);
    }

    public void updateParticipantRole(
            UUID eventId, UUID userId, UUID participantId, Map<String, Object> body) {

        var newRole = (String) body.get("role");
        if (Objects.isNull(newRole)) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsFinalized(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, userId);
        participantsServiceValidator.checkOwnerRole(eventId, userId);
        participantsServiceValidator.checkIsParticipant(eventId, participantId);
        participantsServiceValidator.checkReplaceRole(eventId, participantId);

        participantDAO.updateParticipantRole(eventId, participantId, newRole);
    }

    public String getParticipantRoleInEvent(UUID eventId, UUID participantId) {
        return participantDAO.getParticipantRoleInEvent(eventId, participantId);
    }

    public boolean isOwnerRole(UUID eventId, UUID participantId) {
        return getParticipantRoleInEvent(eventId, participantId)
                .equals(env.getProperty("roles.owner"));
    }

    public boolean isAdminRole(UUID eventId, UUID participantId) {
        return getParticipantRoleInEvent(eventId, participantId)
                .equals(env.getProperty("roles.admin"));
    }

    public boolean isParticipantRole(UUID eventId, UUID participantId) {
        return getParticipantRoleInEvent(eventId, participantId)
                .equals(env.getProperty("roles.participant"));
    }

    public UserBalance getParticipantBalance(UUID eventId, UUID participantId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, participantId);

        return participantBalanceMapper.toUserBalance(
                participantDAO.getParticipantBalance(eventId, participantId));
    }

    public List<ParticipantBalanceSummary> getParticipantsSummaryBalance(
            UUID eventId, UUID participantId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkIsParticipant(eventId, participantId);
        participantsServiceValidator.checkAdminOrOwnerRole(eventId, participantId);

        return participantDAO.getSummaryParticipantBalance(eventId).stream()
                .map(participantSummaryBalanceMapper::toParticipantBalanceSummary)
                .toList();
    }
}
