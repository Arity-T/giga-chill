package com.github.giga_chill.gigachill.service;


import com.github.giga_chill.gigachill.data.access.object.ParticipantDAO;
import com.github.giga_chill.gigachill.data.transfer.object.ParticipantDTO;
import com.github.giga_chill.gigachill.model.Participant;
import com.github.giga_chill.gigachill.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantsService {
    private final Environment env;
    private final ParticipantDAO participantDAO;

    public List<Participant> getAllParticipantsByEventId(String eventId) {
        return participantDAO.getAllParticipantsByEventId(eventId).stream()
                .map(this::toEntity)
                .toList();
    }

    public Participant addParticipantToEvent(String eventId, User user) {
        Participant participant = new Participant(user.id, user.login, user.name,
                env.getProperty("roles.participant").toString(), 0);

        participantDAO.addParticipantToEvent(eventId, toDto(participant));
        return participant;
    }

    public void deleteParticipant(String eventId, String participantId) {
        participantDAO.deleteParticipant(eventId, participantId);
    }

    public boolean isParticipant(String eventId, String userId) {
        return participantDAO.isParticipant(eventId, userId);
    }

    public Participant updateParticipantRole(String eventId, String participantId, String role) {
        participantDAO.updateParticipantRole(eventId, participantId, role);
        return toEntity(participantDAO.getParticipantById(eventId, participantId));
    }

    public String getParticipantRoleInEvent(String eventId, String participantId) {
        return participantDAO.getParticipantRoleInEvent(eventId, participantId);
    }

    public boolean isOwnerRole(String eventId, String participantId) {
        return getParticipantRoleInEvent(eventId, participantId).equals(env.getProperty("roles.owner").toString());
    }

    public boolean isAdminRole(String eventId, String participantId) {
        return getParticipantRoleInEvent(eventId, participantId).equals(env.getProperty("roles.admin").toString());
    }


    private Participant toEntity(ParticipantDTO participantDTO){
        return new Participant(participantDTO.id(),
                participantDTO.login(),
                participantDTO.name(),
                participantDTO.role(),
                participantDTO.balance());
    }

    private ParticipantDTO toDto(Participant participant){
        return new ParticipantDTO(participant.getId(),
                participant.getLogin(),
                participant.getName(),
                participant.getRole(),
                participant.getBalance());
    }

    public boolean isParticipantRole(String eventId, String participantId) {
        return getParticipantRoleInEvent(eventId, participantId).equals(env.getProperty("roles.participant").toString());
    }

}
