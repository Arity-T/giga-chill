package ru.gigachill.repository.simple;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.gigachill.jooq.generated.tables.ConsumerInList;
import ru.gigachill.jooq.generated.tables.ShoppingLists;
import ru.gigachill.jooq.generated.tables.UserInEvent;
import ru.gigachill.jooq.generated.tables.Users;
import ru.gigachill.jooq.generated.tables.records.ConsumerInListRecord;
import ru.gigachill.model.ConsumerWithUserData;

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

        List<ConsumerInListRecord> records =
                userIdsToAdd.stream()
                        .map(
                                userId -> {
                                    ConsumerInListRecord record =
                                            dsl.newRecord(ConsumerInList.CONSUMER_IN_LIST);
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
                                        ConsumerInList.CONSUMER_IN_LIST
                                                .SHOPPING_LIST_ID
                                                .eq(shoppingListId)
                                                .and(
                                                        ConsumerInList.CONSUMER_IN_LIST.USER_ID.eq(
                                                                consumerId))))
                > 0;
    }

    public List<ConsumerWithUserData> findAllConsumersWithUserData(
            UUID shoppingListId, UUID eventId) {
        return dsl.select(
                        ConsumerInList.CONSUMER_IN_LIST.SHOPPING_LIST_ID,
                        ConsumerInList.CONSUMER_IN_LIST.USER_ID,
                        Users.USERS.LOGIN,
                        Users.USERS.NAME,
                        UserInEvent.USER_IN_EVENT.ROLE,
                        UserInEvent.USER_IN_EVENT.BALANCE)
                .from(ConsumerInList.CONSUMER_IN_LIST)
                .join(Users.USERS)
                .on(ConsumerInList.CONSUMER_IN_LIST.USER_ID.eq(Users.USERS.USER_ID))
                .leftJoin(UserInEvent.USER_IN_EVENT)
                .on(
                        ConsumerInList.CONSUMER_IN_LIST
                                .USER_ID
                                .eq(UserInEvent.USER_IN_EVENT.USER_ID)
                                .and(UserInEvent.USER_IN_EVENT.EVENT_ID.eq(eventId)))
                .where(ConsumerInList.CONSUMER_IN_LIST.SHOPPING_LIST_ID.eq(shoppingListId))
                .fetchInto(ConsumerWithUserData.class);
    }

    public List<ConsumerWithUserData> findAllConsumersForEventWithUserData(UUID eventId) {
        return dsl.select(
                        ConsumerInList.CONSUMER_IN_LIST.SHOPPING_LIST_ID,
                        ConsumerInList.CONSUMER_IN_LIST.USER_ID,
                        Users.USERS.LOGIN,
                        Users.USERS.NAME,
                        UserInEvent.USER_IN_EVENT.ROLE,
                        UserInEvent.USER_IN_EVENT.BALANCE)
                .from(ConsumerInList.CONSUMER_IN_LIST)
                .join(ShoppingLists.SHOPPING_LISTS)
                .on(
                        ConsumerInList.CONSUMER_IN_LIST.SHOPPING_LIST_ID.eq(
                                ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID))
                .join(Users.USERS)
                .on(ConsumerInList.CONSUMER_IN_LIST.USER_ID.eq(Users.USERS.USER_ID))
                .leftJoin(UserInEvent.USER_IN_EVENT)
                .on(
                        ConsumerInList.CONSUMER_IN_LIST
                                .USER_ID
                                .eq(UserInEvent.USER_IN_EVENT.USER_ID)
                                .and(UserInEvent.USER_IN_EVENT.EVENT_ID.eq(eventId)))
                .where(ShoppingLists.SHOPPING_LISTS.EVENT_ID.eq(eventId))
                .fetchInto(ConsumerWithUserData.class);
    }
}
