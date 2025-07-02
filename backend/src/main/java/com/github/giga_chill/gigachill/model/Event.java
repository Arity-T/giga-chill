package com.github.giga_chill.gigachill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private String event_id;
    private String user_role;
    private String title;
    private String location;
    private String start_datetime;
    private String end_datetime;
    private String description;
    private Integer budget;
}
