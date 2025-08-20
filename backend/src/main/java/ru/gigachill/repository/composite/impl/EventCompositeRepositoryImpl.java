package ru.gigachill.repository.composite.impl;

import com.github.giga_chill.jooq.generated.enums.EventRole;
import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.gigachill.repository.composite.EventCompositeRepository;
import ru.gigachill.data.transfer.object.EventDTO;
import ru.gigachill.repository.simple.EventRepository;
import ru.gigachill.repository.simple.UserInEventRepository;

@Repository
@RequiredArgsConstructor
public class EventCompositeRepositoryImpl implements EventCompositeRepository {
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
                                                ? eventRecord.getStartDatetime()
                                                : null,
                                        eventRecord.getEndDatetime() != null
                                                ? eventRecord.getEndDatetime()
                                                : null,
                                        eventRecord.getDescription(),
                                        eventRecord.getBudget(),
                                        eventRecord.getIsFinalized()))
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
                                                        ? eventRecord.getStartDatetime()
                                                        : null,
                                                eventRecord.getEndDatetime() != null
                                                        ? eventRecord.getEndDatetime()
                                                        : null,
                                                eventRecord.getDescription(),
                                                eventRecord.getBudget(),
                                                eventRecord.getIsFinalized()));
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
                            if (event.getTitle() != null) eventRecord.setTitle(event.getTitle());
                            if (event.getLocation() != null)
                                eventRecord.setLocation(event.getLocation());
                            if (event.getStartDatetime() != null)
                                eventRecord.setStartDatetime(event.getStartDatetime());
                            if (event.getEndDatetime() != null)
                                eventRecord.setEndDatetime(event.getEndDatetime());
                            if (event.getDescription() != null)
                                eventRecord.setDescription(event.getDescription());
                            // Обновление через dsl
                            eventRecord.update();
                        });
    }

    @Override
    public void createEvent(UUID userId, EventDTO event) {
        EventsRecord eventRecord = new EventsRecord();
        eventRecord.setEventId(event.getEventId());
        eventRecord.setTitle(event.getTitle());
        eventRecord.setLocation(event.getLocation());
        eventRecord.setStartDatetime(
                event.getStartDatetime() != null ? event.getStartDatetime() : null);
        eventRecord.setEndDatetime(event.getEndDatetime() != null ? event.getEndDatetime() : null);
        eventRecord.setDescription(event.getDescription());
        eventRecord.setBudget(event.getBudget());
        eventRepository.save(eventRecord);

        // Привязка пользователя к событию
        UserInEventRecord userInEventRecord = new UserInEventRecord();
        userInEventRecord.setUserId(userId);
        userInEventRecord.setEventId(event.getEventId());
        userInEventRecord.setRole(EventRole.owner);
        userInEventRepository.save(userInEventRecord);
    }

    /**
     * Checks whether an event with the given identifier exists.
     *
     * @param eventId the unique identifier of the event
     * @return {@code true} if the event exists and delete status false, {@code false} otherwise
     */
    @Override
    public boolean isExistedAndNotDeleted(UUID eventId) {
        return eventRepository.existsAndNotDeleted(eventId);
    }

    /**
     * Creates a new invite link record for the specified event.
     *
     * @param eventId the unique identifier of the event
     * @param inviteLinkUuid the UUID to assign as the invite link token
     */
    @Override
    public void createInviteLink(UUID eventId, UUID inviteLinkUuid) {
        eventRepository.updateInviteLink(eventId, inviteLinkUuid);
    }

    /**
     * Retrieves the UUID of the current invite link for the given event.
     *
     * @param eventId the unique identifier of the event
     * @return the {@link UUID} representing the invite link token
     */
    @Override
    public UUID getInviteLinkUuid(UUID eventId) {
        EventsRecord event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return null;

        return event.getInviteToken();
    }

    /**
     * Retrieves the unique Event ID associated with the given invite link UUID.
     *
     * @param linkUuid the UUID token used for event invitation links
     * @return the {@link UUID} of the event linked to the provided invitation token, or {@code
     *     null} if no matching event is found
     */
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
     * @param eventId the unique identifier of the event to recalculate the budget for
     */
    @Override
    public void calculationEventBudget(UUID eventId) {
        eventRepository.refreshDebtsView();

        eventRepository.setEventBudget(eventId, eventRepository.calculateEventBudget(eventId));
    }

    /**
     * Marks the specified event as finalized, preventing further modifications. Executes any
     * finalization logic before setting the event’s status to closed.
     *
     * @param eventId the unique identifier of the event to close
     */
    @Override
    public void finalizeEvent(UUID eventId) {
        eventRepository.finalizeEventById(eventId);
    }

    /**
     * Determines whether the specified event has been finalized (closed).
     *
     * @param eventId the unique identifier of the event to check
     * @return {@code true} if the event is finalized and no longer editable; {@code false}
     *     otherwise
     */
    @Override
    public boolean isFinalized(UUID eventId) {
        return eventRepository.isFinalized(eventId);
    }

    @Override
    public void deleteEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
    }

    /**
     * Retrieves the end date and time of the specified event.
     *
     * @param eventId the unique identifier of the event
     * @return a {@link String} representation of the event’s end date‑time,
     */
    @Override
    public String getEndDatetime(UUID eventId) {
        return eventRepository.getEndDatetimeById(eventId).toString();
    }
}
