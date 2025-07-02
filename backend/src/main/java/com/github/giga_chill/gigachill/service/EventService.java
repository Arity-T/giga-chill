package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.model.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventService {

    //Временно
    private final Map<String, Event> EVENTS = new HashMap<>();
    private final Map<String, List<Event>> USER_EVENTS = new HashMap<>();
    private final Map<String, Map<String, String>> USER_EVENT_ROLES = new HashMap<>();


    public List<Event> getAllUserEvents(String userId){
        //TODO: Связь с бд
        return USER_EVENTS.get(userId);
    }

    public String getUserRoleInEvent(String eventId, String userId){
        //TODO: Связь с бд
        return USER_EVENT_ROLES.get(userId).get(eventId);
    }

}
