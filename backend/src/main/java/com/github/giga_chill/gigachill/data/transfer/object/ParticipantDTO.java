package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.UUID;

public record ParticipantDTO(
        UUID id, String login, String name, String role, @Nullable BigDecimal balance) {}
