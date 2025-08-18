package ru.gigachill.data.transfer.object;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantSummaryBalanceDTO {
    private UserDTO user;
    private BigDecimal totalBalance;
    private ParticipantBalanceDTO userBalance;
}
