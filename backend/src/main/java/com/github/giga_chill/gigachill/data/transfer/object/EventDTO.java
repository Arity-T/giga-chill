package com.github.giga_chill.gigachill.data.transfer.object;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record EventDTO(
        UUID event_id,
        @Nullable String title,
        @Nullable String location,
        @Nullable String start_datetime,
        @Nullable String end_datetime,
        @Nullable String description,
        @Nullable BigDecimal budget) {}
