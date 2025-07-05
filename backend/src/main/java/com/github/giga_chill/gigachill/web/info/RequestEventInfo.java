package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record RequestEventInfo(@NonNull @JsonProperty(required = true) String title,
                               @NonNull @JsonProperty(required = true) String location,
                               @NonNull @JsonProperty(required = true) String start_datetime,
                               @NonNull @JsonProperty(required = true) String end_datetime,
                               @NonNull @JsonProperty(required = true) String description) {
}
