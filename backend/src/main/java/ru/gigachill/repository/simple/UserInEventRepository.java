package ru.gigachill.repository.simple;

import com.github.giga_chill.jooq.generated.enums.EventRole;
import com.github.giga_chill.jooq.generated.tables.UserInEvent;
import com.github.giga_chill.jooq.generated.tables.Users;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.gigachill.model.UserInEventWithUserData;

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

    public List<UserInEventWithUserData> findByEventIdWithUserData(UUID eventId) {
        return dsl.select(
                        UserInEvent.USER_IN_EVENT.USER_ID,
                        UserInEvent.USER_IN_EVENT.EVENT_ID,
                        UserInEvent.USER_IN_EVENT.ROLE,
                        UserInEvent.USER_IN_EVENT.BALANCE,
                        Users.USERS.LOGIN,
                        Users.USERS.NAME)
                .from(UserInEvent.USER_IN_EVENT)
                .join(Users.USERS)
                .on(UserInEvent.USER_IN_EVENT.USER_ID.eq(Users.USERS.USER_ID))
                .where(UserInEvent.USER_IN_EVENT.EVENT_ID.eq(eventId))
                .fetchInto(UserInEventWithUserData.class);
    }

    public boolean existsByEventIdAndUserId(UUID eventId, UUID userId) {
        return dsl.fetchExists(
                dsl.selectFrom(UserInEvent.USER_IN_EVENT)
                        .where(
                                UserInEvent.USER_IN_EVENT
                                        .EVENT_ID
                                        .eq(eventId)
                                        .and(UserInEvent.USER_IN_EVENT.USER_ID.eq(userId))));
    }

    public void updateRole(UUID eventId, UUID userId, EventRole role) {
        dsl.update(UserInEvent.USER_IN_EVENT)
                .set(UserInEvent.USER_IN_EVENT.ROLE, role)
                .where(
                        UserInEvent.USER_IN_EVENT
                                .EVENT_ID
                                .eq(eventId)
                                .and(UserInEvent.USER_IN_EVENT.USER_ID.eq(userId)))
                .execute();
    }

    public EventRole getRole(UUID eventId, UUID userId) {
        return dsl.select(UserInEvent.USER_IN_EVENT.ROLE)
                .from(UserInEvent.USER_IN_EVENT)
                .where(
                        UserInEvent.USER_IN_EVENT
                                .EVENT_ID
                                .eq(eventId)
                                .and(UserInEvent.USER_IN_EVENT.USER_ID.eq(userId)))
                .fetchOne(UserInEvent.USER_IN_EVENT.ROLE);
    }
}
