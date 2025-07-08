package com.github.giga_chill.gigachill.service;


import com.github.giga_chill.gigachill.model.Participant;
import com.github.giga_chill.gigachill.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParticipantsService {
    private final Environment env;

    //TEMPORARY:
    private final Map<String, List<Participant>> EVENT_PARTICIPANTS = new HashMap<>();

    public List<Participant> getAllParticipantsByEventId(String eventId) {
        //TODO: связь с бд

        //TEMPORARY:
        if (!EVENT_PARTICIPANTS.containsKey(eventId)) {
            return List.of();
        }
        return EVENT_PARTICIPANTS.get(eventId);
    }

    public Participant addParticipantToEvent(String eventId, User user) {
        //TODO: связь с бд

        //TEMPORARY:
        Participant participant = new Participant(user.id, user.login, user.name,
                env.getProperty("roles.participant").toString(), 0);
        EVENT_PARTICIPANTS.get(eventId).add(participant);
        return participant;
    }

    //Может и не нужно
    public void createEvent(String eventId, User user) {
        //TODO: связь с бд

        //TEMPORARY:
        Participant participant = new Participant(user.id, user.login, user.name,
                env.getProperty("roles.owner").toString(), 0);
        EVENT_PARTICIPANTS.put(eventId, new ArrayList<>());
        EVENT_PARTICIPANTS.get(eventId).add(participant);
    }

    public void deleteParticipant(String eventId, String participantId) {
        //TODO: связь с бд
        //TODO: Запретить удаление самого себя

        //TEMPORARY:
        EVENT_PARTICIPANTS.get(eventId).removeIf(item -> participantId.equals(item.getId()));
    }

    public boolean isParticipant(String eventId, String userId) {
        //TODO: связь с бд


        //TEMPORARY:
        return EVENT_PARTICIPANTS.get(eventId).stream()
                .anyMatch(item -> userId.equals(item.getId()));
    }

    public Participant updateParticipantRole(String eventId, String participantId, String role) {
        //TODO: связь с бд
        //TODO: менять роль у себя
        //TODO: запретить изменять роль owner

        //TEMPORARY:

        Participant participant = EVENT_PARTICIPANTS.get(eventId)
                .stream()
                .filter(item -> participantId.equals(item.getId()))
                .findFirst()
                .orElse(null);
        participant.setRole(role);
        return participant;
    }

    public String getParticipantRoleInEvent(String eventId, String participantId) {
        //TODO: Связь с бд

        //TEMPORARY:
        return EVENT_PARTICIPANTS.get(eventId)
                .stream()
                .filter(item -> participantId.equals(item.getId()))
                .findFirst()
                .orElse(null).getRole();
    }

    public boolean isOwnerRole(String eventId, String participantId) {
        return getParticipantRoleInEvent(eventId, participantId).equals(env.getProperty("roles.owner").toString());
    }

    public boolean isAdminRole(String eventId, String participantId) {
        return getParticipantRoleInEvent(eventId, participantId).equals(env.getProperty("roles.admin").toString());
    }

    public boolean isParticipantRole(String eventId, String participantId) {
        return getParticipantRoleInEvent(eventId, participantId).equals(env.getProperty("roles.participant").toString());
    }

}
