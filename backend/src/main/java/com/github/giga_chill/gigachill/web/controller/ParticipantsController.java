package com.github.giga_chill.gigachill.web.controller;


import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
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
    //TODO: Настроить подгрузку роли из бд
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_OWNER', ROLE_PARTICIPANT)")
    public ResponseEntity<List<ParticipantInfo>> getParticipants(Authentication authentication,
                                                                 @PathVariable String eventId){
        if (eventService.getEventById(eventId) == null){
            throw new NotFoundException("Мероприятие не найдено");
        }
        return ResponseEntity.ok(participantsService.getAllParticipantsByEventId(eventId)
                .stream()
                .map(this::toParticipantInfo)
                .toList());
    }

    @PostMapping("/{eventId}/participants")
    //TODO: Настроить подгрузку роли из бд
//    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<ParticipantInfo> postParticipant(Authentication authentication, @PathVariable String eventId,
                                                           @RequestBody Map<String, Object> body){
        if (eventService.getEventById(eventId) == null){
            throw new NotFoundException("Мероприятие не найдено");
        }
        String participantLogin = (String) body.get("login");
        User user = inMemoryUserService.getByLogin(participantLogin);
        if (participantsService.IsParticipant(eventId, user.login)){
            throw new ConflictException("Пользователь с таким именем уже является участником мероприятия");
        }
        Participant participant = participantsService.createParticipantInEvent(eventId, user);

        return ResponseEntity.created(URI.create("events/" + eventId + "/participants"))
                .body(toParticipantInfo(participant));
    }



    private ParticipantInfo toParticipantInfo(Participant participant){
        return new ParticipantInfo(participant.getLogin(), participant.getName(),
                participant.getId(), participant.getRole());
    }

}
