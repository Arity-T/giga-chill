package com.github.giga_chill.gigachill.service;


import com.github.giga_chill.gigachill.model.Participant;
import com.github.giga_chill.gigachill.model.Role;
import com.github.giga_chill.gigachill.model.User;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParticipantsService {

    private final Map<String, List<Participant>> EVENT_PARTICIPANTS = new HashMap<>();


    public List<Participant> getAllParticipantsByEventId(String eventId){
        //TODO: связь с бд

        //TEMPORARY:
        if (!EVENT_PARTICIPANTS.containsKey(eventId)) {
            return List.of();
        }
        return EVENT_PARTICIPANTS.get(eventId);
    }

    public Participant createParticipantInEvent(String eventId, User user){
        //TODO: связь с бд

        //TEMPORARY:
        Participant participant = new Participant(user.id, user.login, user.name, Role.ROLE_PARTICIPANT.toString());
        EVENT_PARTICIPANTS.get(eventId).add(participant);
        return participant;
    }

    //Может и не нужно
    public void createEvent(String eventId, User user){
        //TODO: связь с бд

        //TEMPORARY:
        Participant participant = new Participant(user.id, user.login, user.name, Role.ROLE_OWNER.toString());
        EVENT_PARTICIPANTS.put(eventId, new ArrayList<>());
        EVENT_PARTICIPANTS.get(eventId).add(participant);
    }

    public void deleteParticipant(String eventId, String participantId){
        //TODO: связь с бд

        //TEMPORARY:
        EVENT_PARTICIPANTS.get(eventId).removeIf(item -> participantId.equals(item.getId()));
    }

    public boolean IsParticipant(String eventId, String userId){
        //TODO: связь с бд
        //TODO: Запретить удаление самого себя

        //TEMPORARY:
        return EVENT_PARTICIPANTS.get(eventId).stream()
                .anyMatch(item -> userId.equals(item.getId()));
    }

    public Participant updateParticipantRole(String eventId, String participantId, String role){
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


}
