package com.github.giga_chill.gigachill.service;


import com.github.giga_chill.gigachill.data.access.object.ParticipantDAO;
import com.github.giga_chill.gigachill.data.transfer.object.ParticipantDTO;
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
    private final ParticipantDAO participantDAO;

    //TEMPORARY:
//    private final Map<String, List<Participant>> EVENT_PARTICIPANTS = new HashMap<>();

    public List<Participant> getAllParticipantsByEventId(String eventId) {
        //TODO: связь с бд
        return participantDAO.getAllParticipantsByEventId(eventId).stream()
                .map(this::toEntity)
                .toList();



        //TEMPORARY:
//        if (!EVENT_PARTICIPANTS.containsKey(eventId)) {
//            return List.of();
//        }
//        return EVENT_PARTICIPANTS.get(eventId);
    }

    public Participant addParticipantToEvent(String eventId, User user) {
        //TODO: связь с бд
        Participant participant = new Participant(user.id, user.login, user.name,
                env.getProperty("roles.participant").toString());

        participantDAO.addParticipantToEvent(eventId, toDto(participant));
        return participant;
        //TEMPORARY:
//        EVENT_PARTICIPANTS.get(eventId).add(participant);
//        return participant;
    }

    //Может и не нужно
//    public void createEvent(String eventId, User user) {
//        //TODO: связь с бд
//        Participant participant = new Participant(user.id, user.login, user.name, env.getProperty("roles.owner").toString());
//        participantDAO.
//
//        //TEMPORARY:
////        Participant participant = new Participant(user.id, user.login, user.name, env.getProperty("roles.owner").toString());
////        EVENT_PARTICIPANTS.put(eventId, new ArrayList<>());
////        EVENT_PARTICIPANTS.get(eventId).add(participant);
//    }

    public void deleteParticipant(String eventId, String participantId) {
        //TODO: связь с бд
        participantDAO.deleteParticipant(eventId, participantId);

        //TEMPORARY:
//        EVENT_PARTICIPANTS.get(eventId).removeIf(item -> participantId.equals(item.getId()));
    }

    public boolean isParticipant(String eventId, String userId) {
        //TODO: связь с бд
        return participantDAO.isParticipant(eventId, userId);

        //TEMPORARY:
//        return EVENT_PARTICIPANTS.get(eventId).stream()
//                .anyMatch(item -> userId.equals(item.getId()));
    }

    public Participant updateParticipantRole(String eventId, String participantId, String role) {
        //TODO: связь с бд
        //TODO: менять роль у себя
        //TODO: запретить изменять роль owner

        participantDAO.updateParticipantRole(eventId, participantId, role);
        return toEntity(participantDAO.getParticipantById(eventId, participantId));

//        //TEMPORARY:
//
//        Participant participant = EVENT_PARTICIPANTS.get(eventId)
//                .stream()
//                .filter(item -> participantId.equals(item.getId()))
//                .findFirst()
//                .orElse(null);
//        participant.setRole(role);
//        return participant;
    }

    public String getParticipantRoleInEvent(String eventId, String participantId) {
        //TODO: Связь с бд
        return participantDAO.getParticipantRoleInEvent(eventId, participantId);

        //TEMPORARY:
//        return EVENT_PARTICIPANTS.get(eventId)
//                .stream()
//                .filter(item -> participantId.equals(item.getId()))
//                .findFirst()
//                .orElse(null).getRole();
    }

    public boolean isOwner(String eventId, String participantId) {
        return getParticipantRoleInEvent(eventId, participantId).equals(env.getProperty("roles.owner").toString());
    }

    public boolean isAdmin(String eventId, String participantId) {
        return getParticipantRoleInEvent(eventId, participantId).equals(env.getProperty("roles.admin").toString());
    }


    private Participant toEntity(ParticipantDTO participantDTO){
        return new Participant(participantDTO.id(),
                participantDTO.login(),
                participantDTO.name(),
                participantDTO.role());
    }

    private ParticipantDTO toDto(Participant participant){
        return new ParticipantDTO(participant.getId(),
                participant.getLogin(),
                participant.getName(),
                participant.getRole());
    }

}
