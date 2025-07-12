package com.github.giga_chill.gigachill.repository;

import com.github.giga_chill.jooq.generated.tables.ShoppingLists;
import com.github.giga_chill.jooq.generated.tables.records.ShoppingListsRecord;
import io.micrometer.common.lang.Nullable;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

  public void save(ShoppingListsRecord record) {
    dsl.insertInto(ShoppingLists.SHOPPING_LISTS)
            .set(record)
            .execute();
  }

  public void updateTitleAndDescription(UUID shoppingListId, @Nullable String title, @Nullable String description) {
    var updateStep = dsl.update(ShoppingLists.SHOPPING_LISTS);

    // Дубликат для эффективности - можем установить поля за один запрос
    if (title != null && description != null) {
      updateStep
              .set(ShoppingLists.SHOPPING_LISTS.TITLE, title)
              .set(ShoppingLists.SHOPPING_LISTS.DESCRIPTION, description)
              .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(shoppingListId))
              .execute();
      return;
    }

    if (title != null) {
      updateStep
              .set(ShoppingLists.SHOPPING_LISTS.TITLE, title)
              .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(shoppingListId))
              .execute();
    }

    if (description != null) {
      updateStep
              .set(ShoppingLists.SHOPPING_LISTS.DESCRIPTION, description)
              .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(shoppingListId))
              .execute();
    }
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
                    .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.eq(shoppingListId))
    ) > 0;
  }

  public boolean allExist(List<UUID> ids) {
    int count = dsl.fetchCount(
            dsl.selectFrom(ShoppingLists.SHOPPING_LISTS)
                    .where(ShoppingLists.SHOPPING_LISTS.SHOPPING_LIST_ID.in(ids))
    );
    return count == ids.size();
  }

}
