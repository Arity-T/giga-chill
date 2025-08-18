package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import ru.gigachill.data.transfer.object.ParticipantSummaryBalanceDTO;
import ru.gigachill.web.api.model.ParticipantBalanceSummary;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, ParticipantBalanceMapper.class})
public interface ParticipantSummaryBalanceMapper {

    ParticipantBalanceSummary toParticipantBalanceSummary(ParticipantSummaryBalanceDTO dto);
}
