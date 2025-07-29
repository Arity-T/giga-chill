package com.github.giga_chill.gigachill.service.validator;

import com.github.giga_chill.gigachill.data.access.object.EventDAO;
import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventServiceValidator {

    private final EventDAO eventDAO;

    public void checkIsExistedAndNotDeleted(UUID eventId){
        if (!eventDAO.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
    }

    public void checkIsFinalized(UUID eventId){
        if (eventDAO.isFinalized(eventId)) {
            throw new ConflictException("Event with id " + eventId + " was finalized");
        }
    }

}
