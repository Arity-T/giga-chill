package ru.gigachill.web.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.gigachill.service.ShoppingListService;
import ru.gigachill.service.UserService;
import ru.gigachill.web.api.ShoppingListItemsApi;
import ru.gigachill.web.api.model.ShoppingItemCreate;
import ru.gigachill.web.api.model.ShoppingItemSetPurchased;
import ru.gigachill.web.api.model.ShoppingItemUpdate;

@RestController
@RequiredArgsConstructor
public class ShoppingListItemsController implements ShoppingListItemsApi {
    private final UserService userService;
    private final ShoppingListService shoppingListService;

    @Override
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> createShoppingItem(
            UUID eventId, UUID shoppingListId, ShoppingItemCreate shoppingItemCreate) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListService.addShoppingItem(
                shoppingListId, eventId, user.getId(), shoppingItemCreate);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> deleteShoppingItem(
            UUID eventId, UUID shoppingListId, UUID shoppingItemId) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListService.deleteShoppingItemFromShoppingList(
                shoppingListId, shoppingItemId, eventId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> setShoppingItemPurchased(
            UUID eventId,
            UUID shoppingListId,
            UUID shoppingItemId,
            ShoppingItemSetPurchased shoppingItemSetPurchased) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListService.updateShoppingItemStatus(
                shoppingItemId, eventId, user.getId(), shoppingListId, shoppingItemSetPurchased);
        return ResponseEntity.noContent().build();
    }

    @Override
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> updateShoppingItem(
            UUID eventId,
            UUID shoppingListId,
            UUID shoppingItemId,
            ShoppingItemUpdate shoppingItemUpdate) {
        var user =
                userService.userAuthentication(
                        SecurityContextHolder.getContext().getAuthentication());
        shoppingListService.updateShoppingItem(
                shoppingItemId, eventId, user.getId(), shoppingListId, shoppingItemUpdate);
        return ResponseEntity.noContent().build();
    }
}
