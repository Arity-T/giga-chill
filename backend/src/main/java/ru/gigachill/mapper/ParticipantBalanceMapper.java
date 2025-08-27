package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import ru.gigachill.dto.ParticipantBalanceDTO;
import ru.gigachill.web.api.model.UserBalance;

@Mapper(componentModel = "spring", uses = DebtMapper.class)
public interface ParticipantBalanceMapper {

    UserBalance toUserBalance(ParticipantBalanceDTO dto);
}
