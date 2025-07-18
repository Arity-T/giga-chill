package com.github.giga_chill.gigachill.data.transfer.object;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record EventDTO(
        UUID eventId,
        @Nullable String title,
        @Nullable String location,
        @Nullable String startDatetime,
        @Nullable String endDatetime,
        @Nullable String description,
        @Nullable BigDecimal budget,
        @Nullable Boolean isFinalized) {}
