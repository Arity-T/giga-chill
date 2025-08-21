package ru.gigachill.repository.simple;

import com.github.giga_chill.jooq.generated.tables.UserInEvent;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserInEventRepository {
    private final DSLContext dsl;

    public void save(UserInEventRecord record) {
        dsl.insertInto(UserInEvent.USER_IN_EVENT).set(record).execute();
    }

    public Optional<UserInEventRecord> findById(UUID eventId, UUID userId) {
        return dsl.selectFrom(UserInEvent.USER_IN_EVENT)
                .where(
                        UserInEvent.USER_IN_EVENT
                                .EVENT_ID
                                .eq(eventId)
                                .and(UserInEvent.USER_IN_EVENT.USER_ID.eq(userId)))
                .fetchOptional();
    }

    public List<UserInEventRecord> findByEventId(UUID eventId) {
        return dsl.selectFrom(UserInEvent.USER_IN_EVENT)
                .where(UserInEvent.USER_IN_EVENT.EVENT_ID.eq(eventId))
                .fetch();
    }

    public void deleteById(UUID eventId, UUID userId) {
        dsl.delete(UserInEvent.USER_IN_EVENT)
                .where(
                        UserInEvent.USER_IN_EVENT
                                .EVENT_ID
                                .eq(eventId)
                                .and(UserInEvent.USER_IN_EVENT.USER_ID.eq(userId)))
                .execute();
    }
}
