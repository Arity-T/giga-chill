package com.github.giga_chill.gigachill.util;

import com.github.giga_chill.gigachill.data.transfer.object.*;
import com.github.giga_chill.gigachill.model.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public final class DtoEntityMapper {

    public static User toUserEntity(UserDTO user) {
        return new User(user.getId(), user.getLogin(), user.getName());
    }

    public static UserDTO toUserDto(User user) {
        return new UserDTO(user.getId(), user.getLogin(), user.getName());
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
