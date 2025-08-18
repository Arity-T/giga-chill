package ru.gigachill.web.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.gigachill.service.ParticipantService;
import ru.gigachill.service.UserService;
import ru.gigachill.web.api.ParticipantsApi;
import ru.gigachill.web.api.model.Participant;
import ru.gigachill.web.api.model.ParticipantCreate;
import ru.gigachill.web.api.model.ParticipantSetRole;

@RequiredArgsConstructor
@RestController
public class ParticipantsController implements ParticipantsApi {
    private final UserService userService;
    private final ParticipantService participantService;

    @Override
    // ACCESS: owner, admin
    public ResponseEntity<Void> addParticipant(UUID eventId, ParticipantCreate participantCreate) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        participantService.addParticipantToEvent(eventId, user.getId(), participantCreate);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin
    public ResponseEntity<Void> deleteParticipant(UUID eventId, UUID participantId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        participantService.deleteParticipant(eventId, participantId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<Participant>> getParticipants(UUID eventId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(
                participantService.getAllParticipantsByEventId(eventId, user.getId()));
    }

    @Override
    // ACCESS: owner
    public ResponseEntity<Void> setParticipantRole(
            UUID eventId, UUID participantId, ParticipantSetRole participantSetRole) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());

        participantService.updateParticipantRole(
                eventId, user.getId(), participantId, participantSetRole);
        return ResponseEntity.noContent().build();
    }
}
