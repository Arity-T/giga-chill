package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEventInfo {
    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("user_role")
    private String userRole;

    @JsonProperty("title")
    private String title;

    @JsonProperty("location")
    private String location;

    @JsonProperty("start_datetime")
    private String startDatetime;

    @JsonProperty("end_datetime")
    private String endDatetime;

    @JsonProperty("description")
    private String description;

    @JsonProperty("budget")
    private BigDecimal budget;

    @JsonProperty("is_finalized")
    private Boolean isFinalized;
}
