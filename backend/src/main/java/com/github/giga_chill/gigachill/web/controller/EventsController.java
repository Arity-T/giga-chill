package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.model.UserEntity;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.ParticipantService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.web.info.ParticipantBalanceInfo;
import com.github.giga_chill.gigachill.web.info.ParticipantSummaryBalanceInfo;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import com.github.giga_chill.gigachill.web.info.ResponseEventInfo;
import java.util.Collections;
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
public class EventsController {

    private final EventService eventService;
    private final UserService userService;
    private final ParticipantService participantsService;

    @GetMapping
    // ACCESS: ALL
    public ResponseEntity<List<ResponseEventInfo>> getEvents(Authentication authentication) {
        var user = userService.userAuthentication(authentication);
        var userEvents = eventService.getAllUserEvents(user.getId());

        return ResponseEntity.ok(userEvents.isEmpty() ? null : userEvents);
    }

    @PostMapping
    // ACCESS: ALL
    public ResponseEntity<Void> postEvents(
            @RequestBody RequestEventInfo requestEventInfo, Authentication authentication) {
        var user = userService.userAuthentication(authentication);
        eventService.createEvent(user.getId(), requestEventInfo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}")
    // ACCESS: owner, admin, participant
    public ResponseEntity<ResponseEventInfo> getEventById(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);

        return ResponseEntity.ok(eventService.getEventById(user.getId(), eventId));
    }

    @PatchMapping("/{eventId}")
    // ACCESS: owner, admin
    public ResponseEntity<Void> patchEventById(
            @RequestBody RequestEventInfo requestEventInfo,
            Authentication authentication,
            @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);

        eventService.updateEvent(eventId, user.getId(), requestEventInfo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}")
    // ACCESS: owner
    public ResponseEntity<Void> deleteEventById(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);

        eventService.deleteEvent(eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/invitation-token")
    // ACCESS: owner
    public ResponseEntity<Void> postEventLink(
            Authentication authentication, @PathVariable UUID eventId) {
        UserEntity userEntity = userService.userAuthentication(authentication);

        eventService.createInviteLink(eventId, userEntity.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/invitation-token")
    // ACCESS: admin, owner
    public ResponseEntity<Map<String, String>> getEventLink(
            Authentication authentication, @PathVariable UUID eventId) {
        UserEntity userEntity = userService.userAuthentication(authentication);

        var eventLink = eventService.getInviteLink(eventId, userEntity.getId());
        return ResponseEntity.ok(Collections.singletonMap("invitation_token", eventLink));
    }

    @PostMapping("/join-by-invitation-token")
    // ACCESS: ALL
    public ResponseEntity<Map<String, String>> postJoinByLink(
            Authentication authentication, @RequestBody Map<String, Object> body) {
        UserEntity userEntity = userService.userAuthentication(authentication);

        var eventId = eventService.joinByLink(userEntity, body);
        return ResponseEntity.ok(Collections.singletonMap("event_id", eventId.toString()));
    }

    @PostMapping("/{eventId}/finalize")
    // ACCESS: owner
    public ResponseEntity<Void> postFinalizeEvent(
            Authentication authentication, @PathVariable UUID eventId) {
        UserEntity userEntity = userService.userAuthentication(authentication);

        eventService.finalizeEvent(eventId, userEntity.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/my-balance")
    // ACCESS: owner, admin, participant
    public ResponseEntity<ParticipantBalanceInfo> getParticipantBalance(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);
        return ResponseEntity.ok(participantsService.getParticipantBalance(eventId, user.getId()));
    }

    @GetMapping("/{eventId}/balance-summary")
    // ACCESS: owner, admin
    public ResponseEntity<List<ParticipantSummaryBalanceInfo>> getParticipantsSummaryBalance(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);

        return ResponseEntity.ok(
                participantsService.getParticipantsSummaryBalance(eventId, user.getId()));
    }
}
