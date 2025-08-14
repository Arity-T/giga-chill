package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.web.api.InvitationTokensApi;
import com.github.giga_chill.gigachill.web.api.model.InvitationToken;
import com.github.giga_chill.gigachill.web.api.model.JoinByInvitationToken200Response;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class InvitationTokensController implements InvitationTokensApi {

    private final UserService userService;
    private final EventService eventService;

    @Override
    // ACCESS: owner
    public ResponseEntity<Void> createInvitationToken(UUID eventId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        eventService.createInviteLink(eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: admin, owner
    public ResponseEntity<InvitationToken> getInvitationToken(UUID eventId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(eventService.getInviteLink(eventId, user.getId()));
    }

    @Override
    // ACCESS: ALL
    public ResponseEntity<JoinByInvitationToken200Response> joinByInvitationToken(
            InvitationToken invitationToken) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(eventService.joinByLink(user, invitationToken));
    }
}
