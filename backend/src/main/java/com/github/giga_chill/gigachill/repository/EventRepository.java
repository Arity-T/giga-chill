package com.github.giga_chill.gigachill.repository;

import com.github.giga_chill.jooq.generated.tables.Events;
import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepository {
    private final DSLContext dsl;

    // через конструктор получаем DSLContext — он будет создан в JooqConfig
    public EventRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

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

    public OffsetDateTime getEndDatetimeById(UUID eventId) {
        return dsl.select(Events.EVENTS.END_DATETIME)
                .from(Events.EVENTS)
                .where(Events.EVENTS.EVENT_ID.eq(eventId))
                .fetchOne(Events.EVENTS.END_DATETIME);
    }
}
