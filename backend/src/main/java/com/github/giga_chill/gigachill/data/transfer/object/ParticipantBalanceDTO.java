package com.github.giga_chill.gigachill.data.transfer.object;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ParticipantBalanceDTO(
        List<Map<UserDTO, BigDecimal>> myDebts, List<Map<UserDTO, BigDecimal>> debtsToMe) {}
