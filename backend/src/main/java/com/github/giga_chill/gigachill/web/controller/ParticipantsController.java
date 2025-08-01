package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.exception.*;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.ParticipantsService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.web.info.ParticipantInfo;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class ParticipantsController {

    private final EventService eventService;
    private final UserService userService;
    private final ParticipantsService participantsService;

    @GetMapping("/{eventId}/participants")
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ParticipantInfo>> getParticipants(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        return ResponseEntity.ok(participantsService.getAllParticipantsByEventId(eventId));
    }

    @PostMapping("/{eventId}/participants")
    // ACCESS: owner, admin
    public ResponseEntity<Void> postParticipant(
            Authentication authentication,
            @PathVariable UUID eventId,
            @RequestBody Map<String, Object> body) {

        var user = userService.userAuthentication(authentication);
        var participantLogin = (String) body.get("login");
        if (participantLogin == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        var userToAdd = userService.getByLogin(participantLogin);
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (!participantsService.isOwnerRole(eventId, user.getId())
                && !participantsService.isAdminRole(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " does not have permission to add participants to event with id "
                            + eventId);
        }
        if (userToAdd == null) {
            throw new NotFoundException("User with login '" + participantLogin + "' not found");
        }
        if (participantsService.isParticipant(eventId, userToAdd.getId())) {
            throw new ConflictException(
                    "User with login '"
                            + participantLogin
                            + "' is already a participant of event with id "
                            + eventId);
        }

        participantsService.addParticipantToEvent(eventId, userToAdd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}/participants/{participantId}")
    // ACCESS: owner, admin
    public ResponseEntity<Void> deleteParticipant(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID participantId) {
        var user = userService.userAuthentication(authentication);
        if (user.getId().equals(participantId)) {
            throw new BadRequestException(
                    "User with id " + participantId + " cannot delete themselves");
        }
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (!participantsService.isOwnerRole(eventId, user.getId())
                && !participantsService.isAdminRole(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " does not have permission to remove participants from event with id "
                            + eventId);
        }
        if (!participantsService.isParticipant(eventId, participantId)) {
            throw new NotFoundException(
                    "Participant with id " + participantId + " not found in event " + eventId);
        }
        participantsService.deleteParticipant(eventId, participantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{eventId}/participants/{participantId}/role")
    // ACCESS: owner
    public ResponseEntity<Void> patchParticipantRole(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID participantId,
            @RequestBody Map<String, Object> body) {
        var user = userService.userAuthentication(authentication);
        var newRole = (String) body.get("role");
        if (newRole == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!eventService.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (eventService.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (!participantsService.isOwnerRole(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " does not have permission to change participant roles in event with id "
                            + eventId);
        }
        if (!participantsService.isParticipant(eventId, participantId)) {
            throw new NotFoundException(
                    "Participant with id " + participantId + " not found in event " + eventId);
        }
        if (participantsService.isOwnerRole(eventId, participantId)) {
            throw new ConflictException(
                    "The role: owner of the user with id: "
                            + participantId
                            + " cannot be replaced");
        }
        participantsService.updateParticipantRole(eventId, participantId, newRole);

        return ResponseEntity.noContent().build();
    }
}
