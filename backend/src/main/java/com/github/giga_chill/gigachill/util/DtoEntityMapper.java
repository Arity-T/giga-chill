package com.github.giga_chill.gigachill.util;

import com.github.giga_chill.gigachill.data.transfer.object.*;
import com.github.giga_chill.gigachill.model.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public final class DtoEntityMapper {

    public static User toUserEntity(UserDTO user) {
        return new User(user.id(), user.login(), user.name());
    }

    public static UserDTO toUserDto(User user) {
        return new UserDTO(user.getId(), user.getLogin(), user.getName());
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

    public static ParticipantBalance toParticipantBalanceEntity(
            ParticipantBalanceDTO participantBalanceDTO) {
        return new ParticipantBalance(
                participantBalanceDTO.myDebts().stream()
                        .map(
                                item ->
                                        item.entrySet().stream()
                                                .collect(
                                                        Collectors.toMap(
                                                                key ->
                                                                        DtoEntityMapper
                                                                                .toUserEntity(
                                                                                        key
                                                                                                .getKey()),
                                                                Map.Entry::getValue)))
                        .collect(Collectors.toList()),
                participantBalanceDTO.debtsToMe().stream()
                        .map(
                                item ->
                                        item.entrySet().stream()
                                                .collect(
                                                        Collectors.toMap(
                                                                key ->
                                                                        DtoEntityMapper
                                                                                .toUserEntity(
                                                                                        key
                                                                                                .getKey()),
                                                                Map.Entry::getValue)))
                        .collect(Collectors.toList()));
    }

    public static ParticipantSummaryBalance toParticipantSummaryBalance(
            ParticipantSummaryBalanceDTO participantSummaryBalanceDTO) {
        return new ParticipantSummaryBalance(
                DtoEntityMapper.toUserEntity(participantSummaryBalanceDTO.user()),
                participantSummaryBalanceDTO.totalBalance(),
                DtoEntityMapper.toParticipantBalanceEntity(
                        participantSummaryBalanceDTO.userBalance()));
    }
}
