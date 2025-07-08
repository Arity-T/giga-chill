package com.github.giga_chill.gigachill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private String eventId;
    private String title;
    private String location;
    private String startDatetime;
    private String endDatetime;
    private String description;
    private Integer budget;
}
