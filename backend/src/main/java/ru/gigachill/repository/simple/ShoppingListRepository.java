package ru.gigachill.repository.simple;

import io.micrometer.common.lang.Nullable;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;
import ru.gigachill.jooq.generated.tables.ConsumerInList;
import ru.gigachill.jooq.generated.tables.ShoppingItems;
import ru.gigachill.jooq.generated.tables.ShoppingLists;
import ru.gigachill.jooq.generated.tables.records.ShoppingListsRecord;
import ru.gigachill.model.ShoppingListWithDetails;

@Repository
@RequiredArgsConstructor
public class ShoppingListRepository {
    private final DSLContext dsl;

    public Optional<ShoppingListsRecord> findById(UUID shoppingListId) {
        return dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(shoppingListId))
                .fetchOptional();
    }

    public List<ShoppingListsRecord> findByIds(List<UUID> ids) {
        return dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.in(ids))
                .fetch();
    }

    public List<ShoppingListsRecord> findByEventId(UUID eventId) {
        return dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                .where(ShoppingLists.SHOPPING_LISTS.EVENT_ID.eq(eventId))
                .fetch();
    }

    public List<UUID> findIdsByTaskId(UUID taskId) {
        return dsl.select(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID)
                .from(ShoppingLists.SHOPPING_LISTS)
                .where(ShoppingLists.SHOPPING_LISTS.TASK_ID.eq(taskId))
                .fetchInto(UUID.class);
    }

    public List<ShoppingListsRecord> findByTaskId(UUID taskId) {
        return dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                .where(ShoppingLists.SHOPPING_LISTS.TASK_ID.eq(taskId))
                .fetch();
    }

    public void save(ShoppingListsRecord record) {
        dsl.insertInto(ShoppingLists.SHOPPING_LISTS).set(record).execute();
    }

    public void updateShoppingList(
            UUID shoppingListId, @Nullable String title, @Nullable String description) {
        Map<Field<?>, Object> updates = new HashMap<>();

        if (title != null) {
            updates.put(ShoppingLists.SHOPPING_LISTS.TITLE, title);
        }
        if (description != null) {
            updates.put(ShoppingLists.SHOPPING_LISTS.DESCRIPTION, description);
        }

        if (!updates.isEmpty()) {
            dsl.update(ShoppingLists.SHOPPING_LISTS)
                    .set(updates)
                    .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(shoppingListId))
                    .execute();
        }
    }

    public void updateTaskId(UUID listId, UUID taskId) {
        dsl.update(ShoppingLists.SHOPPING_LISTS)
                .set(ShoppingLists.SHOPPING_LISTS.TASK_ID, taskId)
                .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(listId))
                .execute();
    }

    public void deleteById(UUID shoppingListId) {
        dsl.deleteFrom(ShoppingLists.SHOPPING_LISTS)
                .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(shoppingListId))
                .execute();
    }

    public boolean exists(UUID shoppingListId) {
        // Сделано через count для оптимизации
        return dsl.fetchCount(
                        dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                                .where(
                                        ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(
                                                shoppingListId)))
                > 0;
    }

    public boolean allExist(List<UUID> ids) {
        int count =
                dsl.fetchCount(
                        dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                                .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.in(ids)));
        return count == ids.size();
    }

    public boolean canBind(UUID shoppingListId) {
        return dsl.fetchExists(
                dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                        .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(shoppingListId))
                        .and(ShoppingLists.SHOPPING_LISTS.TASK_ID.isNull()));
    }

    public boolean isBindedToTaskOrNull(UUID shoppingListId, UUID taskId) {
        return dsl.fetchExists(
                dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                        .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(shoppingListId))
                        .and(
                                ShoppingLists.SHOPPING_LISTS
                                        .TASK_ID
                                        .eq(taskId)
                                        .or(ShoppingLists.SHOPPING_LISTS.TASK_ID.isNull())));
    }

    public boolean allCanBeBound(List<UUID> shoppingListIds) {
        int count =
                dsl.fetchCount(
                        dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                                .where(
                                        ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.in(
                                                shoppingListIds))
                                .and(ShoppingLists.SHOPPING_LISTS.TASK_ID.isNull()));
        return count == shoppingListIds.size(); // true, если все свободны
    }

    public void detachAllFromTask(UUID taskId) {
        dsl.update(ShoppingLists.SHOPPING_LISTS)
                .set(ShoppingLists.SHOPPING_LISTS.TASK_ID, (UUID) null)
                .where(ShoppingLists.SHOPPING_LISTS.TASK_ID.eq(taskId))
                .execute();
    }

    public int countAllBindedToThisTaskOrNull(List<UUID> shoppingListIds, UUID taskId) {
        return dsl.fetchCount(
                dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                        .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.in(shoppingListIds))
                        .and(
                                ShoppingLists.SHOPPING_LISTS
                                        .TASK_ID
                                        .eq(taskId)
                                        .or(ShoppingLists.SHOPPING_LISTS.TASK_ID.isNull())));
    }

    public void setBudget(UUID shoppingListId, BigDecimal budget) {
        dsl.update(ShoppingLists.SHOPPING_LISTS)
                .set(ShoppingLists.SHOPPING_LISTS.BUDGET, budget)
                .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(shoppingListId))
                .execute();
    }

    public List<ShoppingListWithDetails> findByEventIdWithDetails(UUID eventId) {
        return dsl.select(
                        ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID,
                        ShoppingLists.SHOPPING_LISTS.TASK_ID,
                        ShoppingLists.SHOPPING_LISTS.EVENT_ID,
                        ShoppingLists.SHOPPING_LISTS.TITLE,
                        ShoppingLists.SHOPPING_LISTS.DESCRIPTION,
                        ShoppingLists.SHOPPING_LISTS.RECEIPT_ID,
                        ShoppingLists.SHOPPING_LISTS.BUDGET,
                        ShoppingItems.SHOPPING_ITEMS.SHOPPING_ITEM_ID,
                        ShoppingItems.SHOPPING_ITEMS.TITLE.as("item_title"),
                        ShoppingItems.SHOPPING_ITEMS.QUANTITY,
                        ShoppingItems.SHOPPING_ITEMS.UNIT,
                        ShoppingItems.SHOPPING_ITEMS.IS_PURCHASED,
                        ConsumerInList.CONSUMER_IN_LIST.USER_ID)
                .from(ShoppingLists.SHOPPING_LISTS)
                .leftJoin(ShoppingItems.SHOPPING_ITEMS)
                .on(
                        ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(
                                ShoppingItems.SHOPPING_ITEMS.SHOPPING_LIST_ID))
                .leftJoin(ConsumerInList.CONSUMER_IN_LIST)
                .on(
                        ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(
                                ConsumerInList.CONSUMER_IN_LIST.SHOPPING_LIST_ID))
                .where(ShoppingLists.SHOPPING_LISTS.EVENT_ID.eq(eventId))
                .fetchInto(ShoppingListWithDetails.class);
    }

    public List<ShoppingListWithDetails> findByTaskIdWithDetails(UUID taskId) {
        return dsl.select(
                        ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID,
                        ShoppingLists.SHOPPING_LISTS.TASK_ID,
                        ShoppingLists.SHOPPING_LISTS.EVENT_ID,
                        ShoppingLists.SHOPPING_LISTS.TITLE,
                        ShoppingLists.SHOPPING_LISTS.DESCRIPTION,
                        ShoppingLists.SHOPPING_LISTS.RECEIPT_ID,
                        ShoppingLists.SHOPPING_LISTS.BUDGET,
                        ShoppingItems.SHOPPING_ITEMS.SHOPPING_ITEM_ID,
                        ShoppingItems.SHOPPING_ITEMS.TITLE.as("item_title"),
                        ShoppingItems.SHOPPING_ITEMS.QUANTITY,
                        ShoppingItems.SHOPPING_ITEMS.UNIT,
                        ShoppingItems.SHOPPING_ITEMS.IS_PURCHASED,
                        ConsumerInList.CONSUMER_IN_LIST.USER_ID)
                .from(ShoppingLists.SHOPPING_LISTS)
                .leftJoin(ShoppingItems.SHOPPING_ITEMS)
                .on(
                        ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(
                                ShoppingItems.SHOPPING_ITEMS.SHOPPING_LIST_ID))
                .leftJoin(ConsumerInList.CONSUMER_IN_LIST)
                .on(
                        ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(
                                ConsumerInList.CONSUMER_IN_LIST.SHOPPING_LIST_ID))
                .where(ShoppingLists.SHOPPING_LISTS.TASK_ID.eq(taskId))
                .fetchInto(ShoppingListWithDetails.class);
    }


}
