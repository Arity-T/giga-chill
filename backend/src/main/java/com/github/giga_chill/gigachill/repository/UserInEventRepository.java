package com.github.giga_chill.gigachill.repository;

import org.springframework.stereotype.Repository;
import org.jooq.DSLContext;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import com.github.giga_chill.jooq.generated.tables.UserInEvent;

import java.util.List;
import java.util.UUID;

@Repository
public class UserInEventRepository {
  private final DSLContext dsl;

  public UserInEventRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public void save(UserInEventRecord record) {
    dsl.insertInto(UserInEvent.USER_IN_EVENT)
        .set(record)
        .execute();
  }

  public List<UserInEventRecord> findByEventId(UUID eventId) {
    return dsl.selectFrom(UserInEvent.USER_IN_EVENT)
            .where(UserInEvent.USER_IN_EVENT.EVENT_ID.eq(eventId))
            .fetch();
  }

  public List<UserInEventRecord> findByUserId(UUID userId) {
    return dsl.selectFrom(UserInEvent.USER_IN_EVENT)
            .where(UserInEvent.USER_IN_EVENT.USER_ID.eq(userId))
            .fetch();
  }
}
