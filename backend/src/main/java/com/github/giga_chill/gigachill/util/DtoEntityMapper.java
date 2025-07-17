package com.github.giga_chill.gigachill.util;

import com.github.giga_chill.gigachill.data.transfer.object.*;
import com.github.giga_chill.gigachill.model.*;
import java.util.ArrayList;

public final class DtoEntityMapper {

    public static User toUserEntity(UserDTO user) {
        return new User(user.id(), user.login(), user.name());
    }

    public static UserDTO toUserDto(User user) {
        return new UserDTO(user.getId(), user.getLogin(), user.getName());
    }

    public static Event toEventEntity(EventDTO eventDTO) {
        return new Event(
                eventDTO.eventId(),
                eventDTO.title(),
                eventDTO.location(),
                eventDTO.startDatetime(),
                eventDTO.endDatetime(),
                eventDTO.description(),
                eventDTO.budget());
    }

    public static EventDTO toEventDto(Event event) {
        return new EventDTO(
                event.getEventId(),
                event.getTitle(),
                event.getLocation(),
                event.getStartDatetime(),
                event.getEndDatetime(),
                event.getDescription(),
                event.getBudget());
    }

    public static Participant toParticipantEntity(ParticipantDTO participantDTO) {
        return new Participant(
                participantDTO.id(),
                participantDTO.login(),
                participantDTO.name(),
                participantDTO.role(),
                participantDTO.balance());
    }

    public static ParticipantDTO toParticipantDto(Participant participant) {
        return new ParticipantDTO(
                participant.getId(),
                participant.getLogin(),
                participant.getName(),
                participant.getRole(),
                participant.getBalance());
    }

    public static ShoppingList toShoppingListEntity(ShoppingListDTO shoppingListDTO) {
        return new ShoppingList(
                shoppingListDTO.shoppingListId(),
                shoppingListDTO.taskId(),
                shoppingListDTO.title(),
                shoppingListDTO.description(),
                null,
                shoppingListDTO.budget(),
                shoppingListDTO.shoppingItems().stream()
                        .map(DtoEntityMapper::toShoppingItemEntity)
                        .toList(),
                shoppingListDTO.consumers().stream()
                        .map(DtoEntityMapper::toParticipantEntity)
                        .toList());
    }

    public static ShoppingListDTO toShoppingListDto(ShoppingList shoppingList) {
        return new ShoppingListDTO(
                shoppingList.getShoppingListId(),
                shoppingList.getTaskId(),
                shoppingList.getTitle(),
                shoppingList.getDescription(),
                shoppingList.getBudget(),
                shoppingList.getShoppingItems().stream()
                        .map(DtoEntityMapper::toShoppingItemDto)
                        .toList(),
                shoppingList.getConsumers().stream()
                        .map(DtoEntityMapper::toParticipantDto)
                        .toList());
    }

    public static ShoppingItemDTO toShoppingItemDto(ShoppingItem shoppingItem) {
        return new ShoppingItemDTO(
                shoppingItem.getShoppingItemId(),
                shoppingItem.getTitle(),
                shoppingItem.getQuantity(),
                shoppingItem.getUnit(),
                shoppingItem.getIsPurchased());
    }

    public static ShoppingItem toShoppingItemEntity(ShoppingItemDTO shoppingItemDTO) {
        return new ShoppingItem(
                shoppingItemDTO.shoppingItemId(),
                shoppingItemDTO.title(),
                shoppingItemDTO.quantity(),
                shoppingItemDTO.unit(),
                shoppingItemDTO.isPurchased());
    }

    public static Task toTaskEntity(TaskDTO TaskDTO) {
        return new Task(
                TaskDTO.taskId(),
                TaskDTO.title(),
                TaskDTO.description(),
                TaskDTO.status(),
                TaskDTO.deadlineDatetime(),
                TaskDTO.executorComment(),
                TaskDTO.reviewerComment(),
                DtoEntityMapper.toUserEntity(TaskDTO.author()),
                TaskDTO.executor() != null
                        ? DtoEntityMapper.toUserEntity(TaskDTO.executor())
                        : null,
                new ArrayList<>());
    }

    public static Task toTaskEntity(TaskWithShoppingListsDTO taskWithShoppingListsDTO) {
        return new Task(
                taskWithShoppingListsDTO.taskId(),
                taskWithShoppingListsDTO.title(),
                taskWithShoppingListsDTO.description(),
                taskWithShoppingListsDTO.status(),
                taskWithShoppingListsDTO.deadlineDatetime(),
                taskWithShoppingListsDTO.executorComment(),
                taskWithShoppingListsDTO.reviewerComment(),
                DtoEntityMapper.toUserEntity(taskWithShoppingListsDTO.author()),
                taskWithShoppingListsDTO.executor() != null
                        ? DtoEntityMapper.toUserEntity(taskWithShoppingListsDTO.executor())
                        : null,
                taskWithShoppingListsDTO.shoppingLists().stream()
                        .map(DtoEntityMapper::toShoppingListEntity)
                        .toList());
    }

    public static TaskDTO toTaskDto(Task task) {
        return new TaskDTO(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadlineDatetime(),
                task.getExecutorComment(),
                task.getReviewerComment(),
                task.getAuthor() != null ? DtoEntityMapper.toUserDto(task.getAuthor()) : null,
                task.getExecutor() != null ? DtoEntityMapper.toUserDto(task.getExecutor()) : null);
    }

    public static TaskWithShoppingListsDTO toTaskWithShoppingListsDto(Task task) {
        return new TaskWithShoppingListsDTO(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadlineDatetime(),
                task.getExecutorComment(),
                task.getReviewerComment(),
                DtoEntityMapper.toUserDto(task.getAuthor()),
                task.getExecutor() != null ? DtoEntityMapper.toUserDto(task.getExecutor()) : null,
                task.getShoppingLists().stream().map(DtoEntityMapper::toShoppingListDto).toList());
    }
}
