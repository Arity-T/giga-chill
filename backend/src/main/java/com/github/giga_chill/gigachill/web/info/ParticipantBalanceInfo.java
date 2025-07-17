package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ParticipantBalanceInfo(
        @JsonProperty("my_debts") List<Map<UserInfo, BigDecimal>> myDebts,
        @JsonProperty("debts_to_me") List<Map<UserInfo, BigDecimal>> debtsToMe) {}
