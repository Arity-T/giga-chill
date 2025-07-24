package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantSummaryBalanceInfo {
    @JsonProperty("user")
    private UserInfo user;

    @JsonProperty("total_balance")
    private BigDecimal totalBalance;

    @JsonProperty("user_balance")
    private ParticipantBalanceInfo userBalance;
}
