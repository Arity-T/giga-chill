package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

public record RequestEventInfo(
        @Nullable @JsonProperty("title") String title,
        @Nullable @JsonProperty("location") String location,
        @Nullable @JsonProperty("start_datetime") String startDatetime,
        @Nullable @JsonProperty("end_datetime") String endDatetime,
        @Nullable @JsonProperty("description") String description) {}
