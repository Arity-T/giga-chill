package ru.gigachill.repository.simple;

import static org.jooq.impl.DSL.sum;

import com.github.giga_chill.jooq.generated.enums.TaskStatus;
import com.github.giga_chill.jooq.generated.tables.DebtsPerEvent;
import com.github.giga_chill.jooq.generated.tables.Events;
import com.github.giga_chill.jooq.generated.tables.ShoppingLists;
import com.github.giga_chill.jooq.generated.tables.Tasks;
import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepository {
    private final DSLContext dsl;

    public void save(EventsRecord record) {
        dsl.insertInto(Events.EVENTS).set(record).execute();
    }

    public Optional<EventsRecord> findById(UUID eventId) {
        return dsl.selectFrom(Events.EVENTS)
                .where(Events.EVENTS.EVENT_ID.eq(eventId).and(Events.EVENTS.IS_DELETED.eq(false)))
                .fetchOptional();
    }

    public boolean existsAndNotDeleted(UUID eventId) {
        return dsl.fetchExists(
                dsl.selectFrom(Events.EVENTS)
                        .where(Events.EVENTS.EVENT_ID.eq(eventId))
                        .andNot(Events.EVENTS.IS_DELETED));
    }

    public void deleteById(UUID eventId) {
        dsl.update(Events.EVENTS)
                .set(Events.EVENTS.IS_DELETED, true)
                .where(Events.EVENTS.EVENT_ID.eq(eventId))
                .execute();
    }

    public void updateInviteLink(UUID eventId, UUID inviteLinkUuid) {
        dsl.update(Events.EVENTS)
                .set(Events.EVENTS.INVITE_TOKEN, inviteLinkUuid)
                .where(Events.EVENTS.EVENT_ID.eq(eventId))
                .execute();
    }

    public Optional<EventsRecord> findByLinkId(UUID linkId) {
        return dsl.selectFrom(Events.EVENTS)
                .where(Events.EVENTS.INVITE_TOKEN.eq(linkId))
                .fetchOptional();
    }

    public boolean exists(UUID eventId) {
        return dsl.fetchExists(
                dsl.selectFrom(Events.EVENTS).where(Events.EVENTS.EVENT_ID.eq(eventId)));
    }

    public OffsetDateTime getEndDatetimeById(UUID eventId) {
        return dsl.select(Events.EVENTS.END_DATETIME)
                .from(Events.EVENTS)
                .where(Events.EVENTS.EVENT_ID.eq(eventId))
                .fetchOne(Events.EVENTS.END_DATETIME);
    }

    public void finalizeEventById(UUID eventId) {
        dsl.update(Events.EVENTS)
                .set(Events.EVENTS.IS_FINALIZED, true)
                .where(Events.EVENTS.EVENT_ID.eq(eventId))
                .execute();
    }

    public boolean isFinalized(UUID eventId) {
        return Boolean.TRUE.equals(
                dsl.select(Events.EVENTS.IS_FINALIZED)
                        .from(Events.EVENTS)
                        .where(Events.EVENTS.EVENT_ID.eq(eventId))
                        .fetchOne(Events.EVENTS.IS_FINALIZED));
    }

    public void refreshDebtsView() {
        dsl.execute("REFRESH MATERIALIZED VIEW debts_per_event");
    }

    public BigDecimal calculateEventBudget(UUID eventId) {
        BigDecimal result =
                dsl.select(sum(ShoppingLists.SHOPPING_LISTS.BUDGET))
                        .from(ShoppingLists.SHOPPING_LISTS)
                        .join(Tasks.TASKS)
                        .on(ShoppingLists.SHOPPING_LISTS.TASK_ID.eq(Tasks.TASKS.TASK_ID))
                        .where(
                                ShoppingLists.SHOPPING_LISTS
                                        .EVENT_ID
                                        .eq(eventId)
                                        .and(Tasks.TASKS.STATUS.eq(TaskStatus.completed)))
                        .fetchOne(0, BigDecimal.class);
        return result != null ? result : BigDecimal.ZERO;
    }

    public void setEventBudget(UUID eventId, BigDecimal budget) {
        dsl.update(Events.EVENTS)
                .set(Events.EVENTS.BUDGET, budget)
                .where(Events.EVENTS.EVENT_ID.eq(eventId))
                .execute();
    }

    // Возвращает список долгов в мероприятии eventId, где userId — должник
    public List<Map.Entry<UUID, BigDecimal>> findDebtsICreated(UUID eventId, UUID userId) {
        return dsl.select(
                        DebtsPerEvent.DEBTS_PER_EVENT.CREDITOR_ID,
                        DebtsPerEvent.DEBTS_PER_EVENT.AMOUNT)
                .from(DebtsPerEvent.DEBTS_PER_EVENT)
                .where(
                        DebtsPerEvent.DEBTS_PER_EVENT
                                .EVENT_ID
                                .eq(eventId)
                                .and(DebtsPerEvent.DEBTS_PER_EVENT.DEBTOR_ID.eq(userId)))
                .fetch()
                .map(
                        r ->
                                Map.entry(
                                        r.get(DebtsPerEvent.DEBTS_PER_EVENT.CREDITOR_ID),
                                        r.get(DebtsPerEvent.DEBTS_PER_EVENT.AMOUNT)));
    }

    // Возвращает список долгов в мероприятии eventId, где userId — кредитор
    public List<Map.Entry<UUID, BigDecimal>> findDebtsToMe(UUID eventId, UUID userId) {
        return dsl.select(
                        DebtsPerEvent.DEBTS_PER_EVENT.DEBTOR_ID,
                        DebtsPerEvent.DEBTS_PER_EVENT.AMOUNT)
                .from(DebtsPerEvent.DEBTS_PER_EVENT)
                .where(
                        DebtsPerEvent.DEBTS_PER_EVENT
                                .EVENT_ID
                                .eq(eventId)
                                .and(DebtsPerEvent.DEBTS_PER_EVENT.CREDITOR_ID.eq(userId)))
                .fetch()
                .map(
                        r ->
                                Map.entry(
                                        r.get(DebtsPerEvent.DEBTS_PER_EVENT.DEBTOR_ID),
                                        r.get(DebtsPerEvent.DEBTS_PER_EVENT.AMOUNT)));
    }
}
