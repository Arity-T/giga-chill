package com.github.giga_chill.gigachill.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantSummaryBalance {
    private User user;
    private BigDecimal totalBalance;
    private ParticipantBalance userBalance;
}
