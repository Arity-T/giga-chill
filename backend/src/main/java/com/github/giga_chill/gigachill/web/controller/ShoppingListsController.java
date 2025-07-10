package com.github.giga_chill.gigachill.web.controller;


import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.model.Participant;
import com.github.giga_chill.gigachill.model.ShoppingItem;
import com.github.giga_chill.gigachill.model.ShoppingList;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.service.EventService;
import com.github.giga_chill.gigachill.service.ParticipantsService;
import com.github.giga_chill.gigachill.service.ShoppingListsService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.web.info.ConsumerInfo;
import com.github.giga_chill.gigachill.web.info.ShoppingItemInfo;
import com.github.giga_chill.gigachill.web.info.ShoppingListInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("events/{eventId}/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListsController {

    private final Environment env;
    private final EventService eventService;
    private final UserService userService;
    private final ParticipantsService participantsService;
    private final ShoppingListsService shoppingListsService;

    @GetMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ShoppingListInfo>> getShoppingList(Authentication authentication,
                                                                  @PathVariable UUID eventId) {
        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        List<ShoppingListInfo> shoppingLists = shoppingListsService.getAllShoppingListsFromEvent(eventId).stream()
                .map(item -> toShoppingListInfo(item, canEdit(eventId, item.getShoppingListId(), user.getId())))
                .toList();

        return ResponseEntity.ok(shoppingLists);
    }

    @PostMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> postShoppingList(Authentication authentication,
                                                 @PathVariable UUID eventId,
                                                 @RequestBody Map<String, Object> body) {
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
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        shoppingListsService.createShoppingList(eventId, user.getId(), title, description);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shoppingListId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> patchShoppingList(Authentication authentication,
                                                  @PathVariable UUID eventId,
                                                  @PathVariable UUID shoppingListId,
                                                  @RequestBody Map<String, Object> body) {

        User user = userService.userAuthentication(authentication);
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!shoppingListsService.isExisted(shoppingListId)) {
            throw new NotFoundException("Shopping list with id " + shoppingListId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListId);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListId + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.updateShoppingList(shoppingListId, title, description);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{shoppingListId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> deleteShoppingList(Authentication authentication,
                                                   @PathVariable UUID eventId,
                                                   @PathVariable UUID shoppingListId) {

        User user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!shoppingListsService.isExisted(shoppingListId)) {
            throw new NotFoundException("Shopping list with id " + shoppingListId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListId);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListId + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.deleteShoppingList(shoppingListId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{shoppingListId}/shopping-items")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> postShoppingItem(Authentication authentication,
                                                 @PathVariable UUID eventId,
                                                 @PathVariable UUID shoppingListId,
                                                 @RequestBody Map<String, Object> body) {
        User user = userService.userAuthentication(authentication);
        String title = (String) body.get("title");
        BigDecimal quantity = new BigDecimal((String) body.get("quantity"));
        String unit = (String) body.get("unit");
        if (title == null || quantity == null || unit == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!shoppingListsService.isExisted(shoppingListId)) {
            throw new NotFoundException("Shopping list with id " + shoppingListId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListId);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListId + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.addShoppingItem(shoppingListId, title, quantity, unit);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shoppingListId}/shopping-items/{shoppingItemId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> patchShoppingItem(Authentication authentication,
                                                  @PathVariable UUID eventId,
                                                  @PathVariable UUID shoppingListId,
                                                  @PathVariable UUID shoppingItemId,
                                                  @RequestBody Map<String, Object> body) {
        User user = userService.userAuthentication(authentication);
        String title = (String) body.get("title");
        BigDecimal quantity = new BigDecimal((String) body.get("quantity"));
        String unit = (String) body.get("unit");
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!shoppingListsService.isExisted(shoppingListId)) {
            throw new NotFoundException("Shopping list with id " + shoppingListId + " not found");
        }
        if (!shoppingListsService.isShoppingItemExisted(shoppingItemId)) {
            throw new NotFoundException("Shopping item with id " + shoppingItemId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListId);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListId + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.updateShoppingItem(shoppingItemId, title, quantity, unit);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{shoppingListId}/shopping-items/{shoppingItemId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> deleteShoppingItem(Authentication authentication,
                                                   @PathVariable UUID eventId,
                                                   @PathVariable UUID shoppingListId,
                                                   @PathVariable UUID shoppingItemId) {
        User user = userService.userAuthentication(authentication);
        if (!shoppingListsService.isShoppingItemExisted(shoppingItemId)) {
            throw new NotFoundException("Shopping item with id " + shoppingItemId + " not found");
        }
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!shoppingListsService.isExisted(shoppingListId)) {
            throw new NotFoundException("Shopping list with id " + shoppingListId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListId);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListId + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.deleteShoppingItemFromShoppingList(shoppingListId, shoppingItemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shoppingListId}/shopping-items/{shoppingItemId}/purchased-state")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> patchShoppingItemState(Authentication authentication,
                                                       @PathVariable UUID eventId,
                                                       @PathVariable UUID shoppingListId,
                                                       @PathVariable UUID shoppingItemId,
                                                       @RequestBody Map<String, Object> body) {
        User user = userService.userAuthentication(authentication);
        Boolean isPurchased = (Boolean) body.get("is_purchased");
        if (isPurchased == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!shoppingListsService.isShoppingItemExisted(shoppingItemId)) {
            throw new NotFoundException("Shopping item with id " + shoppingItemId + " not found");
        }
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!shoppingListsService.isExisted(shoppingListId)) {
            throw new NotFoundException("Shopping list with id " + shoppingListId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListId);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListId + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.updateShoppingItemStatus(shoppingItemId, isPurchased);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{shoppingListId}/consumers")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> putConsumers(Authentication authentication,
                                             @PathVariable UUID eventId,
                                             @PathVariable UUID shoppingListId,
                                             @RequestBody List<String> body) {

        User user = userService.userAuthentication(authentication);
        if (body == null || body.isEmpty()) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!shoppingListsService.isExisted(shoppingListId)) {
            throw new NotFoundException("Shopping list with id " + shoppingListId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListId);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListId + " does not" +
                    " have unassigned or assigned status");
        }

        List<UUID> allUsersIds = body.stream()
                .map(this::safeUUID)
                .toList();
        if (!userService.allUsersExistByIds(allUsersIds)) {
            throw new NotFoundException("The list contains a user that is not in the database");
        }

        shoppingListsService.updateShoppingListConsumers(shoppingListId, allUsersIds);
        return ResponseEntity.noContent().build();
    }


    private ShoppingListInfo toShoppingListInfo(ShoppingList shoppingList, Boolean canEdit) {
        return new ShoppingListInfo(shoppingList.getShoppingListId().toString(),
                // TODO: when tasks are added
                shoppingList.getTaskId() != null ? shoppingList.getTaskId().toString() : null,
                shoppingList.getTitle(),
                shoppingList.getDescription(),
                shoppingList.getStatus(),
                canEdit,
                shoppingList.getShoppingItems().stream()
                        .map(this::toShoppingItemInfo)
                        .toList(),
                shoppingList.getConsumers().stream()
                        .map(this::toConsumerInfo)
                        .toList());
    }

    private ConsumerInfo toConsumerInfo(Participant participant) {
        return new ConsumerInfo(participant.getLogin(),
                participant.getName(),
                participant.getId().toString(),
                participant.getRole(),
                participant.getBalance());
    }

    private ShoppingItemInfo toShoppingItemInfo(ShoppingItem shoppingItem) {
        return new ShoppingItemInfo(shoppingItem.getShoppingItemId().toString(),
                shoppingItem.getTitle(),
                shoppingItem.getQuantity(),
                shoppingItem.getUnit(),
                shoppingItem.getIsPurchased());
    }


    private UUID safeUUID(String raw) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID: " + raw);
        }
    }

    public boolean canEdit(UUID eventId, UUID shoppingListId, UUID userId) {
        boolean isParticipant = participantsService.isParticipantRole(eventId, userId);
        boolean isConsumer   = shoppingListsService.isConsumer(shoppingListId, userId);
        if (isParticipant && !isConsumer) {
            return false;
        }

        String shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        boolean isUnassigned = shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned"));
        boolean isAssigned= shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"));
        return (isUnassigned || isAssigned);
    }

}
