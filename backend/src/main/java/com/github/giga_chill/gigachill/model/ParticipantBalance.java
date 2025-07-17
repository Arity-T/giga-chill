package com.github.giga_chill.gigachill.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantBalance {
    private List<Map<User, BigDecimal>> myDebts;
    private List<Map<User, BigDecimal>> debtsToMe;
}
