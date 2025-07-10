package com.github.giga_chill.gigachill.repository;

import com.github.giga_chill.jooq.generated.tables.ConsumerInList;
import com.github.giga_chill.jooq.generated.tables.records.ConsumerInListRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConsumerInListRepository {
    private final DSLContext dsl;

    public List<UUID> findAllConsumers(UUID shoppingListId) {
        return dsl.select(ConsumerInList.CONSUMER_IN_LIST.USER_ID)
                .from(ConsumerInList.CONSUMER_IN_LIST)
                .where(ConsumerInList.CONSUMER_IN_LIST.SHOPPING_LIST_ID.eq(shoppingListId))
                .fetchInto(UUID.class);
    }

    public void deleteConsumers(UUID shoppingListId, List<UUID> userIdsToRemove) {
        if (userIdsToRemove.isEmpty()) return;

        dsl.deleteFrom(ConsumerInList.CONSUMER_IN_LIST)
                .where(ConsumerInList.CONSUMER_IN_LIST.SHOPPING_LIST_ID.eq(shoppingListId))
                .and(ConsumerInList.CONSUMER_IN_LIST.USER_ID.in(userIdsToRemove))
                .execute();
    }

    public void addConsumer(UUID shoppingListId, UUID userId) {
        dsl.insertInto(ConsumerInList.CONSUMER_IN_LIST)
                .set(ConsumerInList.CONSUMER_IN_LIST.SHOPPING_LIST_ID, shoppingListId)
                .set(ConsumerInList.CONSUMER_IN_LIST.USER_ID, userId)
                .execute();
    }

    public void addConsumers(UUID shoppingListId, List<UUID> userIdsToAdd) {
        if (userIdsToAdd.isEmpty()) return;

        List<ConsumerInListRecord> records = userIdsToAdd.stream()
                .map(userId -> {
                    ConsumerInListRecord record = dsl.newRecord(ConsumerInList.CONSUMER_IN_LIST);
                    record.setShoppingListId(shoppingListId);
                    record.setUserId(userId);
                    return record;
                })
                .toList();

        dsl.batchInsert(records).execute();
    }

    public boolean isConsumer(UUID shoppingListId, UUID consumerId) {
        // Сделано через count для оптимизации
        return dsl.fetchCount(
                dsl.selectFrom(ConsumerInList.CONSUMER_IN_LIST)
                        .where(
                                ConsumerInList.CONSUMER_IN_LIST.SHOPPING_LIST_ID.eq(shoppingListId)
                                        .and(ConsumerInList.CONSUMER_IN_LIST.USER_ID.eq(consumerId))

                        )
        ) > 0;
    }
}
