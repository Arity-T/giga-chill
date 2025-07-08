package com.github.giga_chill.gigachill.repository;

import org.springframework.stereotype.Repository;
import org.jooq.DSLContext;
import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;
import com.github.giga_chill.jooq.generated.tables.Events;

import java.util.Optional;
import java.util.UUID;

@Repository
public class EventRepository {
  private final DSLContext dsl;

  // через конструктор получаем DSLContext — он будет создан в JooqConfig
  public EventRepository(DSLContext dsl) {
      this.dsl = dsl;
  }

  public void save(EventsRecord record) {
    dsl.insertInto(Events.EVENTS)
        .set(record)
        .execute();
  }

  public Optional<EventsRecord> findById(UUID eventId) {
    return dsl.selectFrom(Events.EVENTS)
        .where(Events.EVENTS.EVENT_ID.eq(eventId))
        .fetchOptional();
  }
}
