package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ParticipantBalanceInfo(
        @JsonProperty("my_debts") List<DebtInfo> myDebts,
        @JsonProperty("debts_to_me") List<DebtInfo> debtsToMe) {}
