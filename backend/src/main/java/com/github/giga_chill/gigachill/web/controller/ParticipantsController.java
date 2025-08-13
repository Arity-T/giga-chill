package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.service.ParticipantService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.web.api.ParticipantsApi;
import com.github.giga_chill.gigachill.web.api.model.Participant;
import com.github.giga_chill.gigachill.web.api.model.ParticipantCreate;
import com.github.giga_chill.gigachill.web.api.model.ParticipantSetRole;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

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
