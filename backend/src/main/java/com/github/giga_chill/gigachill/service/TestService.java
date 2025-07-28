package com.github.giga_chill.gigachill.service;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("test")
public class TestService {
    private final DSLContext dsl;

    public void cleanBD() {
        Table<?>[] tables = dsl.meta().getTables().toArray(new Table<?>[0]);

        // Оборачиваем в транзакцию
        dsl.transaction((configuration) -> {
            DSLContext ctx = DSL.using(configuration);
            for (Table<?> table : tables) {
                ctx.truncate(table)
                        .cascade()
                        .execute();
            }
        });
    }
}
