package com.github.giga_chill.gigachill.data.transfer.object;

public record EventDTO(String event_id, String title, String location, String start_datetime,
                       String end_datetime, String description, Integer budget) {
}
