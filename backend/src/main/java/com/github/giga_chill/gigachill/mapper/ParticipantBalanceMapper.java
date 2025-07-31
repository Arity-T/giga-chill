package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ParticipantBalanceDTO;
import com.github.giga_chill.gigachill.web.info.ParticipantBalanceInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DebtMapper.class)
public interface ParticipantBalanceMapper {

    ParticipantBalanceInfo toParticipantBalanceInfo(ParticipantBalanceDTO dto);
}
