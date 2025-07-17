package com.github.giga_chill.gigachill.repository;

import com.github.giga_chill.jooq.generated.tables.Users;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final DSLContext dsl;

    // через конструктор получаем DSLContext — он будет создан в JooqConfig
    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public void save(UsersRecord record) {
        dsl.insertInto(Users.USERS).set(record).execute();
    }

    public Optional<UsersRecord> findByLogin(String login) {
        return dsl.selectFrom(Users.USERS).where(Users.USERS.LOGIN.eq(login)).fetchOptional();
    }

    public Optional<UsersRecord> findById(UUID id) {
        return dsl.selectFrom(Users.USERS).where(Users.USERS.USER_ID.eq(id)).fetchOptional();
    }

    /** Возвращает количество пользователей с переданными id */
    public int countByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return dsl.selectCount()
                .from(Users.USERS)
                .where(Users.USERS.USER_ID.in(ids))
                .fetchOne(0, int.class);
    }
}
