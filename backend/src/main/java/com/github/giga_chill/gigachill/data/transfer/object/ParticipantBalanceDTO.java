package com.github.giga_chill.gigachill.data.transfer.object;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantBalanceDTO {
    private List<Map<UserDTO, BigDecimal>> myDebts;
    private List<Map<UserDTO, BigDecimal>> debtsToMe;
}
