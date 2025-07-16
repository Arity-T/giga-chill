package com.github.giga_chill.gigachill.repository;

import com.github.giga_chill.jooq.generated.tables.Events;
import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepository {
    private final DSLContext dsl;

    public void save(EventsRecord record) {
        dsl.insertInto(Events.EVENTS).set(record).execute();
    }

    public Optional<EventsRecord> findById(UUID eventId) {
        return dsl.selectFrom(Events.EVENTS)
                .where(Events.EVENTS.EVENT_ID.eq(eventId).and(Events.EVENTS.IS_DELETED.eq(false)))
                .fetchOptional();
    }

    public void deleteById(UUID eventId) {
        dsl.update(Events.EVENTS)
                .set(Events.EVENTS.IS_DELETED, true)
                .where(Events.EVENTS.EVENT_ID.eq(eventId))
                .execute();
    }

    public void updateInviteLink(UUID eventId, UUID inviteLinkUuid) {
        dsl.update(Events.EVENTS)
                .set(Events.EVENTS.INVITE_LINK, inviteLinkUuid)
                .where(Events.EVENTS.EVENT_ID.eq(eventId))
                .execute();
    }

    public Optional<EventsRecord> findByLinkId(UUID linkId) {
        return dsl.selectFrom(Events.EVENTS)
                .where(Events.EVENTS.INVITE_LINK.eq(linkId))
                .fetchOptional();
    }

    public boolean exists(UUID eventId) {
        return dsl.fetchExists(
                dsl.selectFrom(Events.EVENTS).where(Events.EVENTS.EVENT_ID.eq(eventId)));
    public OffsetDateTime getEndDatetimeById(UUID eventId) {
        return dsl.select(Events.EVENTS.END_DATETIME)
                .from(Events.EVENTS)
                .where(Events.EVENTS.EVENT_ID.eq(eventId))
                .fetchOne(Events.EVENTS.END_DATETIME);
    }
}
