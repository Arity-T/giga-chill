package com.github.giga_chill.gigachill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private UUID eventId;
    private String title;
    private String location;
    private String startDatetime;
    private String endDatetime;
    private String description;
    private BigDecimal budget;
}
