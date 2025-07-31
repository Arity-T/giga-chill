package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ParticipantSummaryBalanceDTO;
import com.github.giga_chill.gigachill.web.info.ParticipantSummaryBalanceInfo;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, ParticipantBalanceMapper.class})
public interface ParticipantSummaryBalanceMapper {

    ParticipantSummaryBalanceInfo toParticipantSummaryBalanceInfo(ParticipantSummaryBalanceDTO dto);
}
