package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ParticipantSummaryBalanceDTO;
import com.github.giga_chill.gigachill.web.api.model.ParticipantBalanceSummary;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, ParticipantBalanceMapper.class})
public interface ParticipantSummaryBalanceMapper {

    ParticipantBalanceSummary toParticipantBalanceSummary(ParticipantSummaryBalanceDTO dto);
}
