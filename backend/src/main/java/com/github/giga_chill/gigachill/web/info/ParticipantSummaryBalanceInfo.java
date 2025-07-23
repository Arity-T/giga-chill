package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record ParticipantSummaryBalanceInfo(
        @JsonProperty("user") UserInfo user,
        @JsonProperty("total_balance") BigDecimal totalBalance,
        @JsonProperty("user_balance") ParticipantBalanceInfo userBalance) {}
