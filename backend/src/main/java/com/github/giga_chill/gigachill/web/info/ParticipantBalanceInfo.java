package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantBalanceInfo {
    @JsonProperty("my_debts")
    private List<DebtInfo> myDebts;

    @JsonProperty("debts_to_me")
    private List<DebtInfo> debtsToMe;
}
