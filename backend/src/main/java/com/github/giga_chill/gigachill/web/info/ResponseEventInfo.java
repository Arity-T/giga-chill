package com.github.giga_chill.gigachill.web.info;

import java.math.BigDecimal;

public record ResponseEventInfo(
        String event_id,
        String user_role,
        String title,
        String location,
        String start_datetime,
        String end_datetime,
        String description,
        BigDecimal budget) {}
