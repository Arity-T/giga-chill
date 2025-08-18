package ru.gigachill.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Meta;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Profile("test")
public class TestService {
    private final DSLContext dsl;

    @Transactional
    public void cleanBD() {
        Meta meta = dsl.meta();
        List<Table<?>> allTables =
                meta.getTables().stream()
                        .filter(
                                t ->
                                        t.getSchema() != null
                                                && "public"
                                                        .equalsIgnoreCase(t.getSchema().getName()))
                        .collect(Collectors.toList());
        // Обычные VIEW
        List<String> viewNames =
                dsl.select(DSL.field("table_name", String.class))
                        .from("information_schema.views")
                        .where(DSL.field("table_schema").eq("public"))
                        .fetchInto(String.class);

        // (Опционально) материализованные вью в Postgres
        List<String> mviewNames =
                dsl.select(DSL.field("matviewname", String.class))
                        .from("pg_matviews")
                        .where(DSL.field("schemaname").eq("public"))
                        .fetchInto(String.class);

        // объединить, если нужно
        Set<String> allViews = new HashSet<>(viewNames);
        allViews.addAll(mviewNames);

        // Фильтруем только настоящие таблицы (исключаем VIEW)
        List<Table<?>> tables =
                allTables.stream()
                        .filter(t -> !allViews.contains(t.getName()))
                        .collect(Collectors.toList());

        // Если таблиц нет — выходим
        if (tables.isEmpty()) {
            return;
        }

        // Определяем диалект
        SQLDialect dialect = dsl.configuration().dialect();
        boolean isPostgres = dialect == SQLDialect.POSTGRES;

        // Выполняем транзакционную очистку
        dsl.transaction(
                cfg -> {
                    DSLContext ctx = DSL.using(cfg);

                    if (isPostgres) {
                        // Postgres: одно командное TRUNCATE всех таблиц
                        ctx.truncate(tables).restartIdentity().cascade().execute();
                    } else {
                        // H2 и другие: простое TRUNCATE по одной таблице
                        for (Table<?> table : tables) {
                            ctx.truncate(table).execute();
                        }
                    }
                });
    }
}
