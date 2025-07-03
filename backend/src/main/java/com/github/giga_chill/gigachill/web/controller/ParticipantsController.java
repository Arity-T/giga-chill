package com.github.giga_chill.gigachill.web.controller;


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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        User user = inMemoryUserService.userAuthentication(authentication);
        if (eventService.getEventById(eventId) == null){
            throw new NotFoundException("Мероприятие не найдено");
        }
        return ResponseEntity.ok(participantsService.getAllParticipantsByEventId(eventId)
                .stream()
                .map(this::toParticipantInfo)
                .toList());
    }



    private ParticipantInfo toParticipantInfo(Participant participant){
        return new ParticipantInfo(participant.getLogin(), participant.getName(),
                participant.getId(), participant.getRole());
    }

}
