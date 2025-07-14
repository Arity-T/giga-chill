package com.github.giga_chill.gigachill.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.jooq.DSLContext;
import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;
import com.github.giga_chill.jooq.generated.tables.Events;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EventRepository {
  private final DSLContext dsl;

  public void save(EventsRecord record) {
    dsl.insertInto(Events.EVENTS)
        .set(record)
        .execute();
  }

  public Optional<EventsRecord> findById(UUID eventId) {
    return dsl.selectFrom(Events.EVENTS)
        .where(
            Events.EVENTS.EVENT_ID.eq(eventId)
            .and(Events.EVENTS.IS_DELETED.eq(false))
        )
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

  public boolean isCorrectLink(UUID eventId, UUID linkUuid) {
    return dsl.fetchExists(
            dsl.selectFrom(Events.EVENTS)
            .where(Events.EVENTS.EVENT_ID.eq(eventId))
            .and(Events.EVENTS.INVITE_LINK.eq(linkUuid))
    );
  }
}
