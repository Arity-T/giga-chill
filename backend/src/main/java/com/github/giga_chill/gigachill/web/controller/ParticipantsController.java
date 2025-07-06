package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.exception.*;
import com.github.giga_chill.gigachill.model.Participant;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.InMemoryUserService;
import com.github.giga_chill.gigachill.service.ParticipantsService;
import com.github.giga_chill.gigachill.web.info.ParticipantInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class ParticipantsController {

    private final EventService eventService;
    private final InMemoryUserService inMemoryUserService;
    private final ParticipantsService participantsService;


    @GetMapping("/{eventId}/participants")
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ParticipantInfo>> getParticipants(Authentication authentication,
                                                                 @PathVariable String eventId) {
        User user = inMemoryUserService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.id)) {
            throw new ForbiddenException("User with id " + user.id +
                    " is not a participant of event with id " + eventId);
        }
        return ResponseEntity.ok(participantsService.getAllParticipantsByEventId(eventId)
                .stream()
                .map(this::toParticipantInfo)
                .toList());
    }

    @PostMapping("/{eventId}/participants")
    // ACCESS: owner
    public ResponseEntity<ParticipantInfo> postParticipant(Authentication authentication,
                                                           @PathVariable String eventId,
                                                           @RequestBody Map<String, Object> body) {

        User user = inMemoryUserService.userAuthentication(authentication);
        String participantLogin = (String) body.get("login");
        if (participantLogin == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        User userToAdd = inMemoryUserService.getByLogin(participantLogin);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.id)) {
            throw new ForbiddenException("User with id " + user.id +
                    " is not a participant of event with id " + eventId);
        }
        if (!participantsService.isOwner(eventId, user.id)) {
            throw new ForbiddenException("User with id " + user.id +
                    " does not have permission to add participants to event with id " + eventId);
        }
        if (userToAdd == null) {
            throw new UnauthorizedException("User with login '" + participantLogin + "' not found");
        }
        if (participantsService.isParticipant(eventId, userToAdd.id)) {
            throw new ConflictException("User with login '" + participantLogin +
                    "' is already a participant of event with id " + eventId);
        }

        Participant participant = participantsService.addParticipantToEvent(eventId, userToAdd);
        return ResponseEntity.created(URI.create("events/" + eventId + "/participants"))
                .body(toParticipantInfo(participant));
    }

    @DeleteMapping("/{eventId}/participants/{participantId}")
    // ACCESS: owner
    public ResponseEntity<Void> deleteParticipant(Authentication authentication,
                                                  @PathVariable String eventId,
                                                  @PathVariable String participantId) {
        User user = inMemoryUserService.userAuthentication(authentication);
        if (user.id.equals(participantId)) {
            throw new BadRequestException("User with id " + participantId + " cannot delete themselves");
        }
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.id)) {
            throw new ForbiddenException("User with id " + user.id +
                    " is not a participant of event with id " + eventId);
        }
        if (!participantsService.isOwner(eventId, user.id)) {
            throw new ForbiddenException("User with id " + user.id +
                    " does not have permission to remove participants from event with id " + eventId);
        }
        if (!participantsService.isParticipant(eventId, participantId)) {
            throw new NotFoundException("Participant with id " + participantId + " not found in event " + eventId);
        }
        participantsService.deleteParticipant(eventId, participantId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/participants/{participantId}/role")
    // ACCESS: owner
    public ResponseEntity<ParticipantInfo> postParticipant(Authentication authentication,
                                                            @PathVariable String eventId,
                                                            @PathVariable String participantId,
                                                            @RequestBody Map<String, Object> body) {
        User user = inMemoryUserService.userAuthentication(authentication);
        String newRole = (String) body.get("role");
        if (newRole == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.id)) {
            throw new ForbiddenException("User with id " + user.id +
                    " is not a participant of event with id " + eventId);
        }
        if (!participantsService.isOwner(eventId, user.id)) {
            throw new ForbiddenException("User with id " + user.id +
                    " does not have permission to change participant roles in event with id " + eventId);
        }
        if (!participantsService.isParticipant(eventId, participantId)) {
            throw new NotFoundException("Participant with id " + participantId + " not found in event " + eventId);
        }

        Participant participant = participantsService.updateParticipantRole(eventId, participantId, newRole);

        return ResponseEntity.ok(toParticipantInfo(participant));
    }

    private ParticipantInfo toParticipantInfo(Participant participant) {
        return new ParticipantInfo(
                participant.getLogin(),
                participant.getName(),
                participant.getId(),
                participant.getRole()
        );
    }

}
