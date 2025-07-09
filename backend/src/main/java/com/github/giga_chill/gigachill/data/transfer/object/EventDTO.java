package com.github.giga_chill.gigachill.data.transfer.object;


import org.springframework.lang.Nullable;

import java.math.BigDecimal;

public record EventDTO(String event_id,
                       @Nullable String title,
                       @Nullable String location,
                       @Nullable String start_datetime,
                       @Nullable String end_datetime,
                       @Nullable String description,
                       @Nullable BigDecimal budget) {
}
