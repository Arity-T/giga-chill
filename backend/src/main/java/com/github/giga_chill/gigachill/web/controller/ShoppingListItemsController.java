package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.service.ShoppingListService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.web.api.ShoppingListItemsApi;
import com.github.giga_chill.gigachill.web.api.model.ShoppingItemCreate;
import com.github.giga_chill.gigachill.web.api.model.ShoppingItemSetPurchased;
import com.github.giga_chill.gigachill.web.api.model.ShoppingItemUpdate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

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
