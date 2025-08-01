package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.service.*;
import com.github.giga_chill.gigachill.web.info.ShoppingListInfo;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("events/{eventId}/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListController {
    private final UserService userService;
    private final ShoppingListService shoppingListsService;

    @GetMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ShoppingListInfo>> getShoppingList(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);

        return ResponseEntity.ok(
                shoppingListsService.getAllShoppingListsFromEvent(eventId, user.getId()));
    }

    @PostMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> postShoppingList(
            Authentication authentication,
            @PathVariable UUID eventId,
            @RequestBody Map<String, Object> body) {

        var user = userService.userAuthentication(authentication);
        shoppingListsService.createShoppingList(eventId, user.getId(), body);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shoppingListId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> patchShoppingList(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID shoppingListId,
            @RequestBody Map<String, Object> body) {

        var user = userService.userAuthentication(authentication);
        shoppingListsService.updateShoppingList(eventId, user.getId(), shoppingListId, body);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{shoppingListId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> deleteShoppingList(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID shoppingListId) {

        var user = userService.userAuthentication(authentication);
        shoppingListsService.deleteShoppingList(shoppingListId, eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{shoppingListId}/shopping-items")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> postShoppingItem(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID shoppingListId,
            @RequestBody Map<String, Object> body) {
        var user = userService.userAuthentication(authentication);
        shoppingListsService.addShoppingItem(shoppingListId, eventId, user.getId(), body);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shoppingListId}/shopping-items/{shoppingItemId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> patchShoppingItem(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID shoppingListId,
            @PathVariable UUID shoppingItemId,
            @RequestBody Map<String, Object> body) {
        var user = userService.userAuthentication(authentication);
        shoppingListsService.updateShoppingItem(
                shoppingItemId, eventId, user.getId(), shoppingListId, body);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{shoppingListId}/budget")
    // ACCESS: owner, admin, participant(если исполнитель)
    public ResponseEntity<Void> putBudget(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID shoppingListId,
            @RequestBody Map<String, Object> body) {
        var user = userService.userAuthentication(authentication);
        shoppingListsService.setBudget(shoppingListId, eventId, user.getId(), body);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{shoppingListId}/shopping-items/{shoppingItemId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> deleteShoppingItem(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID shoppingListId,
            @PathVariable UUID shoppingItemId) {
        var user = userService.userAuthentication(authentication);
        shoppingListsService.deleteShoppingItemFromShoppingList(
                shoppingListId, shoppingItemId, eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shoppingListId}/shopping-items/{shoppingItemId}/purchased-state")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> patchShoppingItemState(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID shoppingListId,
            @PathVariable UUID shoppingItemId,
            @RequestBody Map<String, Object> body) {
        var user = userService.userAuthentication(authentication);
        shoppingListsService.updateShoppingItemStatus(
                shoppingItemId, eventId, user.getId(), shoppingListId, body);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{shoppingListId}/consumers")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> putConsumers(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID shoppingListId,
            @RequestBody List<String> body) {
        var user = userService.userAuthentication(authentication);
        shoppingListsService.updateShoppingListConsumers(
                shoppingListId, eventId, user.getId(), body);
        return ResponseEntity.noContent().build();
    }
}
