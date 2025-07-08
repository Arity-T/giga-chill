package com.github.giga_chill.gigachill.web.info;

public record ResponseEventInfo(String event_id, String user_role, String title, String location, String start_datetime,
                                String end_datetime, String description, Integer budget) {
}
