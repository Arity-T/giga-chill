package ru.gigachill.repository.composite.impl;

import com.github.giga_chill.jooq.generated.enums.EventRole;
import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.gigachill.dto.EventDTO;
import ru.gigachill.mapper.jooq.EventsRecordMapper;
import ru.gigachill.repository.composite.EventCompositeRepository;
import ru.gigachill.repository.simple.EventRepository;
import ru.gigachill.repository.simple.UserInEventRepository;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class EventCompositeRepositoryImpl implements EventCompositeRepository {
    private final EventRepository eventRepository;
    private final UserInEventRepository userInEventRepository;
    private final EventsRecordMapper eventsRecordMapper;

    @Override
    public EventDTO getEventById(UUID eventId) {
        return eventRepository.findById(eventId).map(eventsRecordMapper::toEventDTO).orElse(null);
    }

    @Override
    public List<EventDTO> getAllUserEvents(UUID userId) {
        return eventRepository.findByUserIdWithJoin(userId).stream()
                .map(eventsRecordMapper::toEventDTO)
                .toList();
    }

    @Transactional
    @Override
    public void updateEvent(UUID eventId, EventDTO event) {
        eventRepository
                .findById(eventId)
                .ifPresent(
                        eventRecord -> {
                            // Обновление через маппер: игнорируем null-поля
                            eventsRecordMapper.updateEventsRecordFromDTO(event, eventRecord);
                            // Обновление через dsl
                            eventRecord.update();
                        });
    }

    /**
     * Creates a new event and automatically assigns the creator as the owner.
     *
     * <p>This method performs an atomic operation that: 1. Creates the event record in the database
     * 2. Automatically links the creator user to the event with 'owner' role
     */
    @Transactional
    @Override
    public void createEvent(UUID userId, EventDTO event) {
        EventsRecord eventRecord = eventsRecordMapper.toEventsRecord(event);
        eventRepository.save(eventRecord);

        // Automatically link creator as event owner
        UserInEventRecord userInEventRecord = new UserInEventRecord();
        userInEventRecord.setUserId(userId);
        userInEventRecord.setEventId(event.getEventId());
        userInEventRecord.setRole(EventRole.owner);
        userInEventRepository.save(userInEventRecord);
    }

    @Override
    public boolean isExistedAndNotDeleted(UUID eventId) {
        return eventRepository.existsAndNotDeleted(eventId);
    }

    @Transactional
    @Override
    public void createInviteLink(UUID eventId, UUID inviteLinkUuid) {
        eventRepository.updateInviteLink(eventId, inviteLinkUuid);
    }

    @Override
    public UUID getInviteLinkUuid(UUID eventId) {
        EventsRecord event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return null;

        return event.getInviteToken();
    }

    @Nullable
    @Override
    public UUID getEventByLinkUuid(UUID linkUuid) {
        EventsRecord event = eventRepository.findByLinkId(linkUuid).orElse(null);
        if (event == null) {
            return null;
        }

        return event.getEventId();
    }

    /**
     * Calculates and updates the overall budget for the specified event.
     *
     * <p>This method performs a budget recalculation by: 1. Refreshing the debts view to ensure
     * latest data is used 2. Calculating the total event budget from all debt relationships 3.
     * Updating the event record with the new budget amount
     */
    @Transactional
    @Override
    public void calculationEventBudget(UUID eventId) {
        eventRepository.refreshDebtsView();

        eventRepository.setEventBudget(eventId, eventRepository.calculateEventBudget(eventId));
    }

    @Transactional
    @Override
    public void finalizeEvent(UUID eventId) {
        eventRepository.finalizeEventById(eventId);
    }

    @Override
    public boolean isFinalized(UUID eventId) {
        return eventRepository.isFinalized(eventId);
    }

    @Transactional
    @Override
    public void deleteEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public String getEndDatetime(UUID eventId) {
        return eventRepository.getEndDatetimeById(eventId).toString();
    }
}
