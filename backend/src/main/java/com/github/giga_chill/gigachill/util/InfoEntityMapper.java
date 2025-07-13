package com.github.giga_chill.gigachill.util;

import com.github.giga_chill.gigachill.model.*;
import com.github.giga_chill.gigachill.web.info.*;

public final class InfoEntityMapper {
    public static ResponseEventInfo toResponseEventInfo(Event event, String userRole) {
        return new ResponseEventInfo(
                event.getEventId().toString(),
                userRole,
                event.getTitle(),
                event.getLocation(),
                event.getStartDatetime(),
                event.getEndDatetime(),
                event.getDescription(),
                event.getBudget());
    }

    public static ParticipantInfo toParticipantInfo(Participant participant) {
        return new ParticipantInfo(
                participant.getLogin(),
                participant.getName(),
                participant.getId().toString(),
                participant.getRole());
    }

    public static ShoppingListInfo toShoppingListInfo(ShoppingList shoppingList, Boolean canEdit) {
        return new ShoppingListInfo(
                shoppingList.getShoppingListId().toString(),
                shoppingList.getTaskId() != null ? shoppingList.getTaskId().toString() : null,
                shoppingList.getTitle(),
                shoppingList.getDescription(),
                shoppingList.getStatus(),
                canEdit,
                shoppingList.getShoppingItems().stream()
                        .map(InfoEntityMapper::toShoppingItemInfo)
                        .toList(),
                shoppingList.getConsumers().stream()
                        .map(InfoEntityMapper::toConsumerInfo)
                        .toList());
    }

    public static ConsumerInfo toConsumerInfo(Participant participant) {
        return new ConsumerInfo(
                participant.getLogin(),
                participant.getName(),
                participant.getId().toString(),
                participant.getRole(),
                participant.getBalance());
    }

    public static ShoppingItemInfo toShoppingItemInfo(ShoppingItem shoppingItem) {
        return new ShoppingItemInfo(
                shoppingItem.getShoppingItemId().toString(),
                shoppingItem.getTitle(),
                shoppingItem.getQuantity(),
                shoppingItem.getUnit(),
                shoppingItem.getIsPurchased());
    }

    public static UserInfo toUserInfo(User user) {
        return new UserInfo(user.getLogin(), user.getName(), user.getId().toString());
    }

    public static ResponseTaskInfo toResponseTaskInfo(Task task) {
        return new ResponseTaskInfo(
                task.getTaskId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadlineDatetime(),
                task.getActualApprovalId() != null ? task.getTaskId().toString() : null,
                InfoEntityMapper.toUserInfo(task.getAuthor()),
                task.getExecutor() != null
                        ? InfoEntityMapper.toUserInfo(task.getExecutor())
                        : null);
    }
}
