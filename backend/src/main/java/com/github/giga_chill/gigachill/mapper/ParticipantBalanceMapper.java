package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ParticipantBalanceDTO;
import com.github.giga_chill.gigachill.web.api.model.UserBalance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DebtMapper.class)
public interface ParticipantBalanceMapper {

    UserBalance toUserBalance(ParticipantBalanceDTO dto);
}
