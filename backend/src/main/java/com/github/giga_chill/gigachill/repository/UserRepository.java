package com.github.giga_chill.gigachill.repository;

import com.github.giga_chill.jooq.generated.tables.Users;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private final DSLContext dsl;

    // через конструктор получаем DSLContext — он будет создан в JooqConfig
    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public void save(UsersRecord record) {
        dsl.insertInto(Users.USERS)
                .set(record)
                .execute();
    }

    public Optional<UsersRecord> findByLogin(String login) {
        return dsl.selectFrom(Users.USERS)
                .where(Users.USERS.LOGIN.eq(login))
                .fetchOptional();
    }

    public Optional<UsersRecord> findById(UUID id) {
        return dsl.selectFrom(Users.USERS)
                .where(Users.USERS.USER_ID.eq(id))
                .fetchOptional();
    }
}
