package com.github.giga_chill.gigachill.data.transfer.object;

import java.math.BigDecimal;

public record ParticipantDTO(String id, String login, String name, String role, BigDecimal balance) {
}
