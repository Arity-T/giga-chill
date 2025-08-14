package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.ParticipantService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.web.api.DebtsApi;
import com.github.giga_chill.gigachill.web.api.model.ParticipantBalanceSummary;
import com.github.giga_chill.gigachill.web.api.model.UserBalance;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DebtsController implements DebtsApi {
    private final UserService userService;
    private final EventService eventService;
    private final ParticipantService participantService;

    @Override
    // ACCESS: owner
    public ResponseEntity<Void> finalizeEvent(UUID eventId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        eventService.finalizeEvent(eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin
    public ResponseEntity<List<ParticipantBalanceSummary>> getBalanceSummary(UUID eventId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(
                participantService.getParticipantsSummaryBalance(eventId, user.getId()));
    }

    @Override
    // ACCESS: owner, admin, participant
    public ResponseEntity<UserBalance> getMyBalance(UUID eventId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(participantService.getParticipantBalance(eventId, user.getId()));
    }
}
