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
    //ACCESS: owner, admin, participant
    public ResponseEntity<List<ParticipantInfo>> getParticipants(Authentication authentication,
                                                                 @PathVariable String eventId) {
        User user = inMemoryUserService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Мероприятие не найдено");
        }
        if (!participantsService.IsParticipant(eventId, user.id)){
            throw new ForbiddenException("Пользователь не является участником мероприятия");
        }
        return ResponseEntity.ok(participantsService.getAllParticipantsByEventId(eventId)
                .stream()
                .map(this::toParticipantInfo)
                .toList());
    }

    @PostMapping("/{eventId}/participants")
    //ACCESS: owner
    public ResponseEntity<ParticipantInfo> postParticipant(Authentication authentication, @PathVariable String eventId,
                                                           @RequestBody Map<String, Object> body) {

        User user = inMemoryUserService.userAuthentication(authentication);
        String participantLogin = (String) body.get("login");
        if (participantLogin == null){
            throw new BadRequestException("Не соответствующие тело запроса");
        }
        User userToAdd = inMemoryUserService.getByLogin(participantLogin);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Мероприятие не найдено");
        }
        if (!participantsService.IsParticipant(eventId, user.id)){
            throw new ForbiddenException("Пользователь не является участником мероприятия");
        }
        if (!participantsService.isOwner(eventId, user.id)){
            throw new ForbiddenException("Недостаточно прав");
        }
        if (userToAdd == null) {
            throw new UnauthorizedException("Пользователь не найден");
        }
        if (participantsService.IsParticipant(eventId, userToAdd.id)) {
            throw new ConflictException("Пользователь с таким логином уже является участником мероприятия");
        }

        Participant participant = participantsService.addParticipantToEvent(eventId, userToAdd);
        return ResponseEntity.created(URI.create("events/" + eventId + "/participants"))
                .body(toParticipantInfo(participant));
    }

    @DeleteMapping("/{eventId}/participants/{participantId}")
    //ACCESS: owner,
    public ResponseEntity<Void> deleteParticipant(Authentication authentication, @PathVariable String eventId,
                                                  @PathVariable String participantId) {
        User user = inMemoryUserService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Мероприятие не найдено");
        }
        if (!participantsService.IsParticipant(eventId, user.id)){
            throw new ForbiddenException("Пользователь не является участником мероприятия");
        }
        if (!participantsService.isOwner(eventId, user.id)){
            throw new ForbiddenException("Недостаточно прав");
        }
        if (!participantsService.IsParticipant(eventId, participantId)) {
            throw new NotFoundException("Пользователь с таким именем не найден");
        }
        participantsService.deleteParticipant(eventId, participantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{eventId}/participants/{participantId}/role")
    //ACCESS: owner
    public ResponseEntity<ParticipantInfo> patchParticipant(Authentication authentication, @PathVariable String eventId,
                                                            @PathVariable String participantId,
                                                            @RequestBody Map<String, Object> body) {
        User user = inMemoryUserService.userAuthentication(authentication);
        String newRole = (String) body.get("role");
        if (newRole == null){
            throw new BadRequestException("Не соответствующие тело запроса");
        }
        if (!participantsService.IsParticipant(eventId, user.id)){
            throw new ForbiddenException("Пользователь не является участником мероприятия");
        }
        if (!participantsService.isOwner(eventId, user.id)){
            throw new ForbiddenException("Недостаточно прав");
        }
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Мероприятие не найдено");
        }
        if (!participantsService.IsParticipant(eventId, participantId)) {
            throw new NotFoundException("Пользователь с таким именем не найден");
        }

        Participant participant = participantsService.updateParticipantRole(eventId, participantId, newRole);

        return ResponseEntity.ok(toParticipantInfo(participant));
    }


    private ParticipantInfo toParticipantInfo(Participant participant) {
        return new ParticipantInfo(participant.getLogin(), participant.getName(),
                participant.getId(), participant.getRole());
    }

}
