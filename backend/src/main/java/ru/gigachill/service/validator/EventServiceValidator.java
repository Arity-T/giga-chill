package ru.gigachill.service.validator;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.gigachill.repository.composite.EventDAO;
import ru.gigachill.exception.ConflictException;
import ru.gigachill.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class EventServiceValidator {

    private final EventDAO eventDAO;

    public void checkIsExistedAndNotDeleted(UUID eventId) {
        if (!eventDAO.isExistedAndNotDeleted(eventId)) {
            throw new NotFoundException("Event with id: " + eventId + " not found");
        }
    }

    public void checkIsNotFinalized(UUID eventId) {
        if (eventDAO.isFinalized(eventId)) {
            throw new ConflictException("Event with id: " + eventId + " was finalized");
        }
    }
}
