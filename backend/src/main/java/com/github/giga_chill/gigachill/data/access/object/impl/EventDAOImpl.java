package com.github.giga_chill.gigachill.data.access.object.impl;

import com.github.giga_chill.gigachill.data.access.object.EventDAO;
import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
import com.github.giga_chill.gigachill.repository.EventRepository;
import com.github.giga_chill.gigachill.repository.UserInEventRepository;
import com.github.giga_chill.jooq.generated.enums.EventRole;
import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventDAOImpl implements EventDAO {
    private final EventRepository eventRepository;
    private final UserInEventRepository userInEventRepository;

    @Override
    public EventDTO getEventById(UUID eventId) {
        return eventRepository
                .findById(eventId)
                .map(
                        eventRecord ->
                                new EventDTO(
                                        eventRecord.getEventId(),
                                        eventRecord.getTitle(),
                                        eventRecord.getLocation(),
                                        eventRecord.getStartDatetime() != null
                                                ? eventRecord.getStartDatetime().toString()
                                                : null,
                                        eventRecord.getEndDatetime() != null
                                                ? eventRecord.getEndDatetime().toString()
                                                : null,
                                        eventRecord.getDescription(),
                                        eventRecord.getBudget()))
                .orElse(null);
    }

    @Override
    public List<EventDTO> getAllUserEvents(UUID userId) {
        List<UserInEventRecord> participants = userInEventRepository.findByUserId(userId);
        List<EventDTO> events = new ArrayList<>();
        for (UserInEventRecord participant : participants) {
            eventRepository
                    .findById(participant.getEventId())
                    .ifPresent(
                            eventRecord -> {
                                events.add(
                                        new EventDTO(
                                                eventRecord.getEventId(),
                                                eventRecord.getTitle(),
                                                eventRecord.getLocation(),
                                                eventRecord.getStartDatetime() != null
                                                        ? eventRecord.getStartDatetime().toString()
                                                        : null,
                                                eventRecord.getEndDatetime() != null
                                                        ? eventRecord.getEndDatetime().toString()
                                                        : null,
                                                eventRecord.getDescription(),
                                                eventRecord.getBudget()));
                            });
        }
        return events;
    }

    @Override
    public void updateEvent(UUID eventId, EventDTO event) {
        eventRepository
                .findById(eventId)
                .ifPresent(
                        eventRecord -> {
                            if (event.title() != null) eventRecord.setTitle(event.title());
                            if (event.location() != null) eventRecord.setLocation(event.location());
                            if (event.startDatetime() != null)
                                eventRecord.setStartDatetime(
                                        OffsetDateTime.parse(event.startDatetime()));
                            if (event.endDatetime() != null)
                                eventRecord.setEndDatetime(
                                        OffsetDateTime.parse(event.endDatetime()));
                            if (event.description() != null)
                                eventRecord.setDescription(event.description());
                            if (event.budget() != null) eventRecord.setBudget(event.budget());
                            // Обновление через dsl
                            eventRecord.update();
                        });
    }

    @Override
    public void createEvent(UUID userId, EventDTO event) {
        EventsRecord eventRecord = new EventsRecord();
        eventRecord.setEventId(event.eventId());
        eventRecord.setTitle(event.title());
        eventRecord.setLocation(event.location());
        eventRecord.setStartDatetime(
                event.startDatetime() != null ? OffsetDateTime.parse(event.startDatetime()) : null);
        eventRecord.setEndDatetime(
                event.endDatetime() != null ? OffsetDateTime.parse(event.endDatetime()) : null);
        eventRecord.setDescription(event.description());
        eventRecord.setBudget(event.budget());
        eventRepository.save(eventRecord);

        // Привязка пользователя к событию
        UserInEventRecord userInEventRecord = new UserInEventRecord();
        userInEventRecord.setUserId(userId);
        userInEventRecord.setEventId(event.eventId());
        userInEventRecord.setRole(EventRole.owner);
        userInEventRepository.save(userInEventRecord);
    }

    @Override
    public void deleteEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
    }

    // todo: optimized method exists() in eventRepository
    @Override
    public boolean isExisted(UUID eventId) {
        return eventRepository.findById(eventId).isPresent();
    }
}
