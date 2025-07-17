package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.data.access.object.ParticipantDAO;
import com.github.giga_chill.gigachill.model.Participant;
import com.github.giga_chill.gigachill.model.ParticipantBalance;
import com.github.giga_chill.gigachill.model.ParticipantSummaryBalance;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.util.DtoEntityMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantsService {
    private final Environment env;
    private final ParticipantDAO participantDAO;

    public List<Participant> getAllParticipantsByEventId(UUID eventId) {
        return participantDAO.getAllParticipantsByEventId(eventId).stream()
                .map(DtoEntityMapper::toParticipantEntity)
                .toList();
    }

    public Participant getParticipantById(UUID eventId, UUID participantId) {
        return DtoEntityMapper.toParticipantEntity(
                participantDAO.getParticipantById(eventId, participantId));
    }

    public void addParticipantToEvent(UUID eventId, User user) {
        var participant =
                new Participant(
                        user.getId(),
                        user.getLogin(),
                        user.getName(),
                        env.getProperty("roles.participant").toString(),
                        BigDecimal.valueOf(0));

        participantDAO.addParticipantToEvent(
                eventId, DtoEntityMapper.toParticipantDto(participant));
    }

    public void deleteParticipant(UUID eventId, UUID participantId) {
        participantDAO.deleteParticipant(eventId, participantId);
    }

    public boolean isParticipant(UUID eventId, UUID userId) {
        return participantDAO.isParticipant(eventId, userId);
    }

    public void updateParticipantRole(UUID eventId, UUID participantId, String role) {
        participantDAO.updateParticipantRole(eventId, participantId, role);
    }

    public String getParticipantRoleInEvent(UUID eventId, UUID participantId) {
        return participantDAO.getParticipantRoleInEvent(eventId, participantId);
    }

    public boolean isOwnerRole(UUID eventId, UUID participantId) {
        return getParticipantRoleInEvent(eventId, participantId)
                .equals(env.getProperty("roles.owner").toString());
    }

    public boolean isAdminRole(UUID eventId, UUID participantId) {
        return getParticipantRoleInEvent(eventId, participantId)
                .equals(env.getProperty("roles.admin").toString());
    }

    public boolean isParticipantRole(UUID eventId, UUID participantId) {
        return getParticipantRoleInEvent(eventId, participantId)
                .equals(env.getProperty("roles.participant").toString());
    }

    public ParticipantBalance getParticipantBalance(UUID eventId, UUID participantId) {
        return DtoEntityMapper.toParticipantBalanceEntity(
                participantDAO.getParticipantBalance(eventId, participantId));
    }

    public List<ParticipantSummaryBalance> getParticipantsSummaryBalance(UUID eventId) {
        return participantDAO.getSummaryParticipantBalance(eventId).stream()
                .map(DtoEntityMapper::toParticipantSummaryBalance)
                .toList();
    }
}
