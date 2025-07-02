package com.github.giga_chill.gigachill.model.info;

public record EventInfo(String event_id, String user_role, String title, String location, String start_datetime,
                        String end_datetime, String description, Integer budget) {
}
