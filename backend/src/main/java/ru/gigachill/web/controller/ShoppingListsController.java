package ru.gigachill.web.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.gigachill.service.ShoppingListService;
import ru.gigachill.service.UserService;
import ru.gigachill.web.api.ShoppingListsApi;
import ru.gigachill.web.api.model.ShoppingListCreate;
import ru.gigachill.web.api.model.ShoppingListSetBudget;
import ru.gigachill.web.api.model.ShoppingListUpdate;
import ru.gigachill.web.api.model.ShoppingListWithItems;

@RestController
@RequiredArgsConstructor
public class ShoppingListsController implements ShoppingListsApi {
    private final UserService userService;
    private final ShoppingListService shoppingListService;

    @Override
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> createShoppingList(
            UUID eventId, ShoppingListCreate shoppingListCreate) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListService.createShoppingList(eventId, user.getId(), shoppingListCreate);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> deleteShoppingList(UUID eventId, UUID shoppingListId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListService.deleteShoppingList(shoppingListId, eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ShoppingListWithItems>> getShoppingLists(UUID eventId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(
                shoppingListService.getAllShoppingListsFromEvent(eventId, user.getId()));
    }

    @Override
    // ACCESS: owner, admin, participant(если исполнитель)
    public ResponseEntity<Void> setShoppingListBudget(
            UUID eventId, UUID shoppingListId, ShoppingListSetBudget shoppingListSetBudget) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListService.setBudget(shoppingListId, eventId, user.getId(), shoppingListSetBudget);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> setShoppingListConsumers(
            UUID eventId, UUID shoppingListId, List<UUID> UUID) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListService.updateShoppingListConsumers(
                shoppingListId, eventId, user.getId(), UUID);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> updateShoppingList(
            UUID eventId, UUID shoppingListId, ShoppingListUpdate shoppingListUpdate) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListService.updateShoppingList(
                eventId, user.getId(), shoppingListId, shoppingListUpdate);
        return ResponseEntity.noContent().build();
    }
}
