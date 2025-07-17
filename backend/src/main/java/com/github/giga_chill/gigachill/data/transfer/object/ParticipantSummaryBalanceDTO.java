package com.github.giga_chill.gigachill.data.transfer.object;

import java.math.BigDecimal;

public record ParticipantSummaryBalanceDTO(
        UserDTO user,
        BigDecimal totalBalance,
        ParticipantBalanceDTO userBalance
) {
}
