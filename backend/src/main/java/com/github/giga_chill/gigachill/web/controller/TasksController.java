package com.github.giga_chill.gigachill.web.controller;


import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.*;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.RequestTaskInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("events/{eventId}/tasks")
@RequiredArgsConstructor
public class TasksController {


    private final Environment env;
    private final EventService eventService;
    private final UserService userService;
    private final ParticipantsService participantsService;
    private final TaskService taskService;

//    @GetMapping
//    public ResponseEntity<>

    @PostMapping
    public ResponseEntity<Void> postTask(Authentication authentication,
                                         @PathVariable UUID eventId,
                                         @RequestBody RequestTaskInfo requestTaskInfo){

        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }

        taskService.createTask(eventId, user, requestTaskInfo);
        return ResponseEntity.noContent().build();
    }
}
