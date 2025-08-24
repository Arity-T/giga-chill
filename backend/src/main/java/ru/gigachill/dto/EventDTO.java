package ru.gigachill.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class EventDTO {
    private UUID eventId;
    @Nullable private String title;
    @Nullable private String location;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Nullable
    private OffsetDateTime startDatetime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Nullable
    private OffsetDateTime endDatetime;

    @Nullable private String description;
    @Nullable private BigDecimal budget;
    @Nullable private Boolean isFinalized;
}
