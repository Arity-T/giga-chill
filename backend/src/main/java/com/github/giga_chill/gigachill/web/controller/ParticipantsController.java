package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.exception.*;
import com.github.giga_chill.gigachill.model.Participant;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.service.ParticipantsService;
import com.github.giga_chill.gigachill.util.UUIDUtils;
import com.github.giga_chill.gigachill.web.info.ParticipantInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class ParticipantsController {

    private final EventService eventService;
    private final UserService userService;
    private final ParticipantsService participantsService;


    @GetMapping("/{eventId}/participants")
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ParticipantInfo>> getParticipants(Authentication authentication,
                                                                 @PathVariable String eventId) {
        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        return ResponseEntity.ok(participantsService.getAllParticipantsByEventId(eventUuid)
                .stream()
                .map(this::toParticipantInfo)
                .toList());
    }

    @PostMapping("/{eventId}/participants")
    // ACCESS: owner, admin
    public ResponseEntity<Void> postParticipant(Authentication authentication,
                                                @PathVariable String eventId,
                                                @RequestBody Map<String, Object> body) {

        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        String participantLogin = (String) body.get("login");
        if (participantLogin == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        User userToAdd = userService.getByLogin(participantLogin);
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        if (!participantsService.isOwnerRole(eventUuid, user.getId()) && !participantsService.isAdminRole(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " does not have permission to add participants to event with id " + eventUuid);
        }
        if (userToAdd == null) {
            throw new NotFoundException("User with login '" + participantLogin + "' not found");
        }
        if (participantsService.isParticipant(eventUuid, userToAdd.getId())) {
            throw new ConflictException("User with login '" + participantLogin +
                    "' is already a participant of event with id " + eventUuid);
        }

        participantsService.addParticipantToEvent(eventUuid, userToAdd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}/participants/{participantId}")
    // ACCESS: owner, admin
    public ResponseEntity<Void> deleteParticipant(Authentication authentication,
                                                  @PathVariable String eventId,
                                                  @PathVariable String participantId) {
        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        UUID participantUuid = UUIDUtils.safeUUID(participantId);
        if (user.getId().equals(participantUuid)) {
            throw new BadRequestException("User with id " + participantUuid + " cannot delete themselves");
        }
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        if (!participantsService.isOwnerRole(eventUuid, user.getId()) && !participantsService.isAdminRole(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " does not have permission to remove participants from event with id " + eventUuid);
        }
        if (!participantsService.isParticipant(eventUuid, participantUuid)) {
            throw new NotFoundException("Participant with id " + participantUuid + " not found in event " + eventUuid);
        }
        participantsService.deleteParticipant(eventUuid, participantUuid);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{eventId}/participants/{participantId}/role")
    // ACCESS: owner
    public ResponseEntity<Void> patchParticipantRole(Authentication authentication,
                                                     @PathVariable String eventId,
                                                     @PathVariable String participantId,
                                                     @RequestBody Map<String, Object> body) {
        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        UUID participantUuid = UUIDUtils.safeUUID(participantId);
        String newRole = (String) body.get("role");
        if (newRole == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        if (!participantsService.isOwnerRole(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " does not have permission to change participant roles in event with id " + eventUuid);
        }
        if (!participantsService.isParticipant(eventUuid, participantUuid)) {
            throw new NotFoundException("Participant with id " + participantUuid + " not found in event " + eventUuid);
        }
        if (participantsService.isOwnerRole(eventUuid, participantUuid)) {
            throw new ConflictException("The role: owner of the user with id: " + participantUuid
                    + " cannot be replaced");
        }
        participantsService.updateParticipantRole(eventUuid, participantUuid, newRole);

        return ResponseEntity.noContent().build();
    }

    private ParticipantInfo toParticipantInfo(Participant participant) {
        return new ParticipantInfo(
                participant.getLogin(),
                participant.getName(),
                participant.getId().toString(),
                participant.getRole()
        );
    }

}
