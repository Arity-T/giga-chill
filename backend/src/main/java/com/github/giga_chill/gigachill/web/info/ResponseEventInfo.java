package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record ResponseEventInfo(
        @JsonProperty("event_id") String eventId,
        @JsonProperty("user_role") String userRole,
        @JsonProperty("title") String title,
        @JsonProperty("location") String location,
        @JsonProperty("start_datetime") String startDatetime,
        @JsonProperty("end_datetime") String endDatetime,
        @JsonProperty("description") String description,
        @JsonProperty("budget") BigDecimal budget) {}
