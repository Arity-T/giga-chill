package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.exception.*;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.ParticipantService;
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
public class ParticipantController {

    private final EventService eventService;
    private final UserService userService;
    private final ParticipantService participantsService;

    @GetMapping("/{eventId}/participants")
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ParticipantInfo>> getParticipants(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);
        return ResponseEntity.ok(
                participantsService.getAllParticipantsByEventId(eventId, user.getId()));
    }

    @PostMapping("/{eventId}/participants")
    // ACCESS: owner, admin
    public ResponseEntity<Void> postParticipant(
            Authentication authentication,
            @PathVariable UUID eventId,
            @RequestBody Map<String, Object> body) {

        var user = userService.userAuthentication(authentication);
        participantsService.addParticipantToEvent(eventId, user.getId(), body);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}/participants/{participantId}")
    // ACCESS: owner, admin
    public ResponseEntity<Void> deleteParticipant(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID participantId) {
        var user = userService.userAuthentication(authentication);
        participantsService.deleteParticipant(eventId, participantId, user.getId());
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

        participantsService.updateParticipantRole(eventId, user.getId(), participantId, body);

        return ResponseEntity.noContent().build();
    }
}
