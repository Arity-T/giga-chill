package com.github.giga_chill.gigachill.data.transfer.object;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class EventDTO {
    private UUID eventId;
    @Nullable private String title;
    @Nullable private String location;
    @Nullable private String startDatetime;
    @Nullable private String endDatetime;
    @Nullable private String description;
    @Nullable private BigDecimal budget;
    @Nullable private Boolean isFinalized;
}
