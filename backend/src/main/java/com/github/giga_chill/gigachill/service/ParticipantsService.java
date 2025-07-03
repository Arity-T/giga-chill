package com.github.giga_chill.gigachill.service;


import com.github.giga_chill.gigachill.model.Participant;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParticipantsService {

    private final Map<String, List<Participant>> EVENT_PARTICIPANTS = new HashMap<>();


    public List<Participant> getAllParticipantsByEventId(String eventId){
        //TODO: связь с бд

        //TEMPORARY:
        if (!EVENT_PARTICIPANTS.containsKey(eventId)) {
            return List.of();
        }
        return EVENT_PARTICIPANTS.get(eventId);
    }






}
