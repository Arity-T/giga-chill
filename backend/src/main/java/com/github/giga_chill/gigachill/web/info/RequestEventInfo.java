package com.github.giga_chill.gigachill.web.info;

import org.springframework.lang.Nullable;

public record RequestEventInfo(
        @Nullable String title,
        @Nullable String location,
        @Nullable String start_datetime,
        @Nullable String end_datetime,
        @Nullable String description) {}
