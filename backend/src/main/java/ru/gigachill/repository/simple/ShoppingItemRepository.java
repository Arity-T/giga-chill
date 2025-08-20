package ru.gigachill.repository.simple;

import com.github.giga_chill.jooq.generated.tables.ShoppingItems;
import com.github.giga_chill.jooq.generated.tables.records.ShoppingItemsRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ShoppingItemRepository {
    private final DSLContext dsl;

    public Optional<ShoppingItemsRecord> findById(UUID shoppingItemId) {
        return dsl.selectFrom(ShoppingItems.SHOPPING_ITEMS)
                .where(ShoppingItems.SHOPPING_ITEMS.SHOPPING_ITEM_ID.eq(shoppingItemId))
                .fetchOptional();
    }

    public List<ShoppingItemsRecord> findByShoppingListId(UUID shoppingListId) {
        return dsl.selectFrom(ShoppingItems.SHOPPING_ITEMS)
                .where(ShoppingItems.SHOPPING_ITEMS.SHOPPING_LIST_ID.eq(shoppingListId))
                .fetch();
    }

    public void save(ShoppingItemsRecord record) {
        dsl.insertInto(ShoppingItems.SHOPPING_ITEMS).set(record).execute();
    }

    public void update(
            UUID shoppingItemId,
            String title,
            BigDecimal quantity,
            String unit,
            Boolean isPurchased) {
        dsl.update(ShoppingItems.SHOPPING_ITEMS)
                .set(ShoppingItems.SHOPPING_ITEMS.TITLE, title)
                .set(ShoppingItems.SHOPPING_ITEMS.QUANTITY, quantity)
                .set(ShoppingItems.SHOPPING_ITEMS.UNIT, unit)
                .set(ShoppingItems.SHOPPING_ITEMS.IS_PURCHASED, isPurchased)
                .where(ShoppingItems.SHOPPING_ITEMS.SHOPPING_ITEM_ID.eq(shoppingItemId))
                .execute();
    }

    public void update(ShoppingItemsRecord record) {
        dsl.update(ShoppingItems.SHOPPING_ITEMS)
                .set(record)
                .where(ShoppingItems.SHOPPING_ITEMS.SHOPPING_ITEM_ID.eq(record.getShoppingItemId()))
                .execute();
    }

    public void updateStatus(UUID shoppingItemId, boolean status) {
        dsl.update(ShoppingItems.SHOPPING_ITEMS)
                .set(ShoppingItems.SHOPPING_ITEMS.IS_PURCHASED, status)
                .where(ShoppingItems.SHOPPING_ITEMS.SHOPPING_ITEM_ID.eq(shoppingItemId))
                .execute();
    }

    public void deleteById(UUID shoppingItemId) {
        dsl.deleteFrom(ShoppingItems.SHOPPING_ITEMS)
                .where(ShoppingItems.SHOPPING_ITEMS.SHOPPING_ITEM_ID.eq(shoppingItemId))
                .execute();
    }

    public boolean exists(UUID shoppingItemId) {
        // Сделано через count для оптимизации
        return dsl.fetchCount(
                        dsl.selectFrom(ShoppingItems.SHOPPING_ITEMS)
                                .where(
                                        ShoppingItems.SHOPPING_ITEMS.SHOPPING_ITEM_ID.eq(
                                                shoppingItemId)))
                > 0;
    }

    public void resetAllStatusByListId(UUID shoppingListId) {
        dsl.update(ShoppingItems.SHOPPING_ITEMS)
                .set(ShoppingItems.SHOPPING_ITEMS.IS_PURCHASED, false)
                .where(ShoppingItems.SHOPPING_ITEMS.SHOPPING_LIST_ID.eq(shoppingListId))
                .execute();
    }

    public void resetAllStatusByListIds(List<UUID> shoppingListIds) {
        dsl.update(ShoppingItems.SHOPPING_ITEMS)
                .set(ShoppingItems.SHOPPING_ITEMS.IS_PURCHASED, false)
                .where(ShoppingItems.SHOPPING_ITEMS.SHOPPING_LIST_ID.in(shoppingListIds))
                .execute();
    }
}
