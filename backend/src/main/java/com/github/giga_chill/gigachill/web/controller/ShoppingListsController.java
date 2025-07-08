package com.github.giga_chill.gigachill.web.controller;


import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.model.ShoppingList;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.ParticipantsService;
import com.github.giga_chill.gigachill.service.ShoppingListsService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.web.info.ShoppingListInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("events/{eventId}/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListsController {

    private final EventService eventService;
    private final UserService userService;
    private final ParticipantsService participantsService;
    private final ShoppingListsService shoppingListsService;

    @GetMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ShoppingListInfo>> getShoppingList(Authentication authentication,
                                                                  @PathVariable String eventId) {
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.id)) {
            throw new ForbiddenException("User with id " + user.id +
                    " is not a participant of event with id " + eventId);
        }

        return ResponseEntity.ok(shoppingListsService.getAllShoppingLists(eventId).stream()
                .map(this::toShoppingListInfo).toList());
    }

    @PostMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<ShoppingListInfo> postShoppingList(Authentication authentication,
                                                             @PathVariable String eventId,
                                                             @RequestBody Map<String, Object> body){

        //TODO: привязка к task id
        User user = userService.userAuthentication(authentication);
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        if (title == null || description == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.id)) {
            throw new ForbiddenException("User with id " + user.id +
                    " is not a participant of event with id " + eventId);
        }

        return ResponseEntity.ok(toShoppingListInfo(
                shoppingListsService.createShoppingList(eventId, title, description)));
    }

    private ShoppingListInfo toShoppingListInfo(ShoppingList shoppingList) {
        return new ShoppingListInfo(shoppingList.getShoppingListId(), shoppingList.getTaskId(), shoppingList.getTitle(),
                shoppingList.getDescription(), shoppingList.getStatus(), shoppingList.getShoppingItems(),
                shoppingList.getConsumers());
    }


}
