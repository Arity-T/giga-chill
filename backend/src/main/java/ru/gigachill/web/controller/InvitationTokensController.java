package ru.gigachill.web.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.gigachill.service.EventService;
import ru.gigachill.service.UserService;
import ru.gigachill.web.api.InvitationTokensApi;
import ru.gigachill.web.api.model.EventId;
import ru.gigachill.web.api.model.InvitationToken;
import ru.gigachill.web.api.model.InvitationTokenJoin;

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
    public ResponseEntity<EventId> joinByInvitationToken(InvitationTokenJoin invitationToken) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(eventService.joinByLink(user, invitationToken));
    }
}
