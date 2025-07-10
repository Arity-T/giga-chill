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
import com.github.giga_chill.gigachill.util.UUIDUtils;
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
                                                                  @PathVariable String eventId) {
        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }

        return ResponseEntity.ok(shoppingListsService.getAllShoppingListsFromEvent(eventUuid).stream()
                .map(this::toShoppingListInfo).toList());
    }

    @PostMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> postShoppingList(Authentication authentication,
                                                 @PathVariable String eventId,
                                                 @RequestBody Map<String, Object> body) {
        //TODO: привязка к task id
        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        if (title == null || description == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        shoppingListsService.createShoppingList(eventUuid, title, description);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shoppingListId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> patchShoppingList(Authentication authentication,
                                                  @PathVariable String eventId,
                                                  @PathVariable String shoppingListId,
                                                  @RequestBody Map<String, Object> body) {

        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        UUID shoppingListUuid = UUIDUtils.safeUUID(shoppingListId);
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!shoppingListsService.isExisted(eventUuid, shoppingListUuid)) {
            throw new NotFoundException("Shopping list with id " + shoppingListUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        if (participantsService.isParticipantRole(eventUuid, user.getId())
                && !shoppingListsService.isConsumer(eventUuid, shoppingListUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListUuid);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(eventUuid, shoppingListUuid);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListUuid + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.updateShoppingList(eventUuid, shoppingListUuid, title, description);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{shoppingListId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> deleteShoppingList(Authentication authentication,
                                                   @PathVariable String eventId,
                                                   @PathVariable String shoppingListId) {

        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        UUID shoppingListUuid = UUIDUtils.safeUUID(shoppingListId);
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!shoppingListsService.isExisted(eventUuid, shoppingListUuid)) {
            throw new NotFoundException("Shopping list with id " + shoppingListUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        if (participantsService.isParticipantRole(eventUuid, user.getId())
                && !shoppingListsService.isConsumer(eventUuid, shoppingListUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListUuid);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(eventUuid, shoppingListUuid);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListUuid + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.deleteShoppingList(eventUuid, shoppingListUuid);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{shoppingListId}/shopping-items")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> postShoppingItem(Authentication authentication,
                                                 @PathVariable String eventId,
                                                 @PathVariable String shoppingListId,
                                                 @RequestBody Map<String, Object> body) {
        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        UUID shoppingListUuid = UUIDUtils.safeUUID(shoppingListId);
        String title = (String) body.get("title");
        BigDecimal quantity = (BigDecimal) body.get("quantity");
        String unit = (String) body.get("unit");
        if (title == null || quantity == null || unit == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!shoppingListsService.isExisted(eventUuid, shoppingListUuid)) {
            throw new NotFoundException("Shopping list with id " + shoppingListUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        if (participantsService.isParticipantRole(eventUuid, user.getId())
                && !shoppingListsService.isConsumer(eventUuid, shoppingListUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListUuid);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(eventUuid, shoppingListUuid);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListUuid + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.addShoppingItem(eventUuid, shoppingListUuid, title, quantity, unit);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shoppingListId}/shopping-items/{shoppingItemId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> patchShoppingItem(Authentication authentication,
                                                  @PathVariable String eventId,
                                                  @PathVariable String shoppingListId,
                                                  @PathVariable String shoppingItemId,
                                                  @RequestBody Map<String, Object> body) {
        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        UUID shoppingListUuid = UUIDUtils.safeUUID(shoppingListId);
        UUID shoppingItemUuid = UUIDUtils.safeUUID(shoppingItemId);
        String title = (String) body.get("title");
        BigDecimal quantity = (BigDecimal) body.get("quantity");
        String unit = (String) body.get("unit");
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!shoppingListsService.isExisted(eventUuid, shoppingListUuid)) {
            throw new NotFoundException("Shopping list with id " + shoppingListUuid + " not found");
        }
        if (!shoppingListsService.isShoppingItemExisted(shoppingListUuid, shoppingItemUuid)) {
            throw new NotFoundException("Shopping item with id " + shoppingItemUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        if (participantsService.isParticipantRole(eventUuid, user.getId())
                && !shoppingListsService.isConsumer(eventUuid, shoppingListUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListUuid);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(eventUuid, shoppingListUuid);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListUuid + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.updateShoppingItem(eventUuid, shoppingListUuid, shoppingItemUuid, title, quantity, unit);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{shoppingListId}/shopping-items/{shoppingItemId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> deleteShoppingItem(Authentication authentication,
                                                   @PathVariable String eventId,
                                                   @PathVariable String shoppingListId,
                                                   @PathVariable String shoppingItemId) {
        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        UUID shoppingListUuid = UUIDUtils.safeUUID(shoppingListId);
        UUID shoppingItemUuid = UUIDUtils.safeUUID(shoppingItemId);
        if (!shoppingListsService.isShoppingItemExisted(shoppingListUuid, shoppingItemUuid)) {
            throw new NotFoundException("Shopping item with id " + shoppingItemUuid + " not found");
        }
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!shoppingListsService.isExisted(eventUuid, shoppingListUuid)) {
            throw new NotFoundException("Shopping list with id " + shoppingListUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        if (participantsService.isParticipantRole(eventUuid, user.getId())
                && !shoppingListsService.isConsumer(eventUuid, shoppingListUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListUuid);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(eventUuid, shoppingListUuid);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListUuid + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.deleteShoppingItemFromShoppingList(eventUuid, shoppingListUuid, shoppingItemUuid);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shoppingListId}/shopping-items/{shoppingItemId}/purchased-state")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> patchShoppingItemState(Authentication authentication,
                                                       @PathVariable String eventId,
                                                       @PathVariable String shoppingListId,
                                                       @PathVariable String shoppingItemId,
                                                       @RequestBody Map<String, Object> body) {
        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        UUID shoppingListUuid = UUIDUtils.safeUUID(shoppingListId);
        UUID shoppingItemUuid = UUIDUtils.safeUUID(shoppingItemId);
        Boolean isPurchased = (Boolean) body.get("is_purchased");
        if (isPurchased == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!shoppingListsService.isShoppingItemExisted(shoppingListUuid, shoppingItemUuid)) {
            throw new NotFoundException("Shopping item with id " + shoppingItemUuid + " not found");
        }
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!shoppingListsService.isExisted(eventUuid, shoppingListUuid)) {
            throw new NotFoundException("Shopping list with id " + shoppingListUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        if (participantsService.isParticipantRole(eventUuid, user.getId())
                && !shoppingListsService.isConsumer(eventUuid, shoppingListUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListUuid);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(eventUuid, shoppingListUuid);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListUuid + " does not" +
                    " have unassigned or assigned status");
        }
        shoppingListsService.updateShoppingItemStatus(eventUuid, shoppingListUuid, shoppingItemUuid, isPurchased);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{shoppingListId}/consumers")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> putConsumers(Authentication authentication,
                                             @PathVariable String eventId,
                                             @PathVariable String shoppingListId,
                                             @RequestBody List<String> body) {

        User user = userService.userAuthentication(authentication);
        UUID eventUuid = UUIDUtils.safeUUID(eventId);
        UUID shoppingListUuid = UUIDUtils.safeUUID(shoppingListId);
        if (body == null || body.isEmpty()) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!eventService.isExisted(eventUuid)) {
            throw new NotFoundException("Event with id " + eventUuid + " not found");
        }
        if (!shoppingListsService.isExisted(eventUuid, shoppingListUuid)) {
            throw new NotFoundException("Shopping list with id " + shoppingListUuid + " not found");
        }
        if (!participantsService.isParticipant(eventUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a participant of event with id " + eventUuid);
        }
        if (participantsService.isParticipantRole(eventUuid, user.getId())
                && !shoppingListsService.isConsumer(eventUuid, shoppingListUuid, user.getId())) {
            throw new ForbiddenException("User with id " + user.getId() +
                    " is not a consumer of shopping list with id " + shoppingListUuid);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(eventUuid, shoppingListUuid);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned")) &&
                !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException("Shopping list with id: " + shoppingListUuid + " does not" +
                    " have unassigned or assigned status");
        }

        List<UUID> allUsersIds = body.stream()
                .map(UUIDUtils::safeUUID)
                .toList();
        if (!userService.allUsersExistByIds(allUsersIds)) {
            throw new NotFoundException("The list contains a user that is not in the database");
        }

        shoppingListsService.updateShoppingListConsumers(eventUuid, shoppingListUuid, body);
        return ResponseEntity.noContent().build();
    }


    private ShoppingListInfo toShoppingListInfo(ShoppingList shoppingList) {
        return new ShoppingListInfo(shoppingList.getShoppingListId().toString(),
                shoppingList.getTaskId().toString(),
                shoppingList.getTitle(),
                shoppingList.getDescription(),
                shoppingList.getStatus(),
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

}
