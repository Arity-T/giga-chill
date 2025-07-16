package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.ForbiddenException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.service.*;
import com.github.giga_chill.gigachill.util.InfoEntityMapper;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.ShoppingListInfo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("events/{eventId}/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListsController {

    private final Environment env;
    private final EventService eventService;
    private final UserService userService;
    private final ParticipantsService participantsService;
    private final ShoppingListsService shoppingListsService;
    private final TaskService taskService;

    @GetMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<List<ShoppingListInfo>> getShoppingList(
            Authentication authentication, @PathVariable UUID eventId) {
        var user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        List<ShoppingListInfo> shoppingLists =
                shoppingListsService.getAllShoppingListsFromEvent(eventId).stream()
                        .map(
                                item ->
                                        InfoEntityMapper.toShoppingListInfo(
                                                item,
                                                canEdit(
                                                        eventId,
                                                        item.getShoppingListId(),
                                                        user.getId())))
                        .toList();

        return ResponseEntity.ok(shoppingLists);
    }

    @PostMapping
    // ACCESS: owner, admin, participant
    public ResponseEntity<Void> postShoppingList(
            Authentication authentication,
            @PathVariable UUID eventId,
            @RequestBody Map<String, Object> body) {
        // TODO: привязка к task id
        var user = userService.userAuthentication(authentication);
        var title = (String) body.get("title");
        var description = (String) body.get("description");
        if (title == null || description == null) {
            throw new BadRequestException("Invalid request body: " + body);
        }
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        shoppingListsService.createShoppingList(eventId, user.getId(), title, description);
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
        var title = (String) body.get("title");
        var description = (String) body.get("description");
        var budget =
                body.get("quantity") != null
                        ? new BigDecimal(String.valueOf((Number) body.get("budget")))
                        : null;
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!shoppingListsService.isExisted(shoppingListId)) {
            throw new NotFoundException("Shopping list with id " + shoppingListId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a consumer of shopping list with id "
                            + shoppingListId);
        }
        var shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned"))
                && !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " does not"
                            + " have unassigned or assigned status");
        }
        shoppingListsService.updateShoppingList(shoppingListId, title, description, budget);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{shoppingListId}")
    // ACCESS: owner, admin, participant(если потребитель)
    public ResponseEntity<Void> deleteShoppingList(
            Authentication authentication,
            @PathVariable UUID eventId,
            @PathVariable UUID shoppingListId) {

        var user = userService.userAuthentication(authentication);
        if (!eventService.isExisted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        if (!shoppingListsService.isExisted(shoppingListId)) {
            throw new NotFoundException("Shopping list with id " + shoppingListId + " not found");
        }
        if (!participantsService.isParticipant(eventId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a consumer of shopping list with id "
                            + shoppingListId);
        }
        var shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned"))
                && !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " does not"
                            + " have unassigned or assigned status");
        }
        shoppingListsService.deleteShoppingList(shoppingListId);

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
        var title = (String) body.get("title");
        var quantity =
                body.get("quantity") != null
                        ? new BigDecimal(String.valueOf((Number) body.get("quantity")))
                        : null;
        var unit = (String) body.get("unit");
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
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a consumer of shopping list with id "
                            + shoppingListId);
        }
        var shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned"))
                && !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " does not"
                            + " have unassigned or assigned status");
        }
        shoppingListsService.addShoppingItem(shoppingListId, title, quantity, unit);
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
        var title = (String) body.get("title");
        var quantity =
                body.get("quantity") != null
                        ? new BigDecimal(String.valueOf((Number) body.get("quantity")))
                        : null;
        var unit = (String) body.get("unit");
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
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a consumer of shopping list with id "
                            + shoppingListId);
        }
        var shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned"))
                && !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " does not"
                            + " have unassigned or assigned status");
        }
        shoppingListsService.updateShoppingItem(shoppingItemId, title, quantity, unit);
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
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a consumer of shopping list with id "
                            + shoppingListId);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned"))
                && !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " does not"
                            + " have unassigned or assigned status");
        }
        shoppingListsService.deleteShoppingItemFromShoppingList(shoppingListId, shoppingItemId);
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
        var isPurchased = (Boolean) body.get("is_purchased");
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
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a consumer of shopping list with id "
                            + shoppingListId);
        }
        var shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.in_progress"))) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " does not"
                            + " have in progress status");
        }
        var taskId = shoppingListsService.getTaskIdForShoppingList(shoppingListId);
        if (taskId == null) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " is not is not attached to the task");
        }
        var executorId = taskService.getExecutorId(taskId);
        var taskStatus = taskService.getTaskStatus(taskId);
        if (!(executorId != null
                        && executorId.equals(user.getId())
                        && taskStatus.equals(env.getProperty("task_status.in_progress")))
                && !(participantsService.isParticipantRole(eventId, user.getId())
                        && taskStatus.equals(env.getProperty("task_status.under_review")))) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " can not change shopping item status in shopping list wih id "
                            + shoppingListId);
        }
        shoppingListsService.updateShoppingItemStatus(shoppingItemId, isPurchased);
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
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a participant of event with id "
                            + eventId);
        }
        if (participantsService.isParticipantRole(eventId, user.getId())
                && !shoppingListsService.isConsumer(shoppingListId, user.getId())) {
            throw new ForbiddenException(
                    "User with id "
                            + user.getId()
                            + " is not a consumer of shopping list with id "
                            + shoppingListId);
        }
        String shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        if (!shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned"))
                && !shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"))) {
            throw new ConflictException(
                    "Shopping list with id: "
                            + shoppingListId
                            + " does not"
                            + " have unassigned or assigned status");
        }

        List<UUID> allUsersIds = body.stream().map(UuidUtils::safeUUID).toList();
        if (!userService.allUsersExistByIds(allUsersIds)) {
            throw new NotFoundException("The list contains a user that is not in the database");
        }

        shoppingListsService.updateShoppingListConsumers(shoppingListId, allUsersIds);
        return ResponseEntity.noContent().build();
    }

    public boolean canEdit(UUID eventId, UUID shoppingListId, UUID userId) {
        var isParticipant = participantsService.isParticipantRole(eventId, userId);
        var isConsumer = shoppingListsService.isConsumer(shoppingListId, userId);
        if (isParticipant && !isConsumer) {
            return false;
        }

        var shoppingListStatus = shoppingListsService.getShoppingListStatus(shoppingListId);
        var isUnassigned =
                shoppingListStatus.equals(env.getProperty("shopping_list_status.unassigned"));
        var isAssigned =
                shoppingListStatus.equals(env.getProperty("shopping_list_status.assigned"));
        return (isUnassigned || isAssigned);
    }
}
