-- Performance indexes for optimizing database queries
-- Based on analysis of repository methods and common query patterns

-- =====================================================
-- USER_IN_EVENT TABLE INDEXES
-- =====================================================

-- Single column indexes for foreign keys
CREATE INDEX IF NOT EXISTS idx_user_in_event_user_id ON user_in_event(user_id);
CREATE INDEX IF NOT EXISTS idx_user_in_event_event_id ON user_in_event(event_id);

-- Composite index for common queries (user_id, event_id) - covers primary key
CREATE INDEX IF NOT EXISTS idx_user_in_event_user_event ON user_in_event(user_id, event_id);

-- =====================================================
-- EVENTS TABLE INDEXES
-- =====================================================

-- Unique field for invite links
CREATE INDEX IF NOT EXISTS idx_events_invite_token ON events(invite_token);

-- Filtering indexes for soft delete and finalization
CREATE INDEX IF NOT EXISTS idx_events_is_deleted ON events(is_deleted);
CREATE INDEX IF NOT EXISTS idx_events_is_finalized ON events(is_finalized);

-- Composite index for active events (most common query pattern)
CREATE INDEX IF NOT EXISTS idx_events_active ON events(is_deleted, is_finalized);

-- =====================================================
-- TASKS TABLE INDEXES
-- =====================================================

-- Single column indexes for foreign keys
CREATE INDEX IF NOT EXISTS idx_tasks_event_id ON tasks(event_id);
CREATE INDEX IF NOT EXISTS idx_tasks_author_id ON tasks(author_id);
CREATE INDEX IF NOT EXISTS idx_tasks_executor_id ON tasks(executor_id);

-- Status filtering index
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_tasks_event_status ON tasks(event_id, status);
CREATE INDEX IF NOT EXISTS idx_tasks_executor_status ON tasks(executor_id, status);

-- =====================================================
-- SHOPPING_LISTS TABLE INDEXES
-- =====================================================

-- Single column indexes for foreign keys
CREATE INDEX IF NOT EXISTS idx_shopping_lists_event_id ON shopping_lists(event_id);
CREATE INDEX IF NOT EXISTS idx_shopping_lists_task_id ON shopping_lists(task_id);

-- Composite index for common query pattern (event_id, task_id)
CREATE INDEX IF NOT EXISTS idx_shopping_lists_event_task ON shopping_lists(event_id, task_id);

-- =====================================================
-- SHOPPING_ITEMS TABLE INDEXES
-- =====================================================

-- Single column index for foreign key
CREATE INDEX IF NOT EXISTS idx_shopping_items_shopping_list_id ON shopping_items(shopping_list_id);

-- Status filtering index for purchase status
CREATE INDEX IF NOT EXISTS idx_shopping_items_is_purchased ON shopping_items(is_purchased);

-- Composite index for common query pattern (shopping_list_id, is_purchased)
CREATE INDEX IF NOT EXISTS idx_shopping_items_list_status ON shopping_items(shopping_list_id, is_purchased);

-- =====================================================
-- CONSUMER_IN_LIST TABLE INDEXES
-- =====================================================

-- Single column indexes for foreign keys
CREATE INDEX IF NOT EXISTS idx_consumer_in_list_user_id ON consumer_in_list(user_id);
CREATE INDEX IF NOT EXISTS idx_consumer_in_list_shopping_list_id ON consumer_in_list(shopping_list_id);

-- Composite index for common query pattern (user_id, shopping_list_id) - covers primary key
CREATE INDEX IF NOT EXISTS idx_consumer_in_list_user_shopping ON consumer_in_list(user_id, shopping_list_id);

-- =====================================================
-- DEBTS_PER_EVENT MATERIALIZED VIEW INDEXES
-- =====================================================

-- Single column indexes for foreign keys
CREATE INDEX IF NOT EXISTS idx_debts_per_event_event_id ON debts_per_event(event_id);
CREATE INDEX IF NOT EXISTS idx_debts_per_event_debtor_id ON debts_per_event(debtor_id);
CREATE INDEX IF NOT EXISTS idx_debts_per_event_creditor_id ON debts_per_event(creditor_id);

-- Composite indexes for common debt query patterns
CREATE INDEX IF NOT EXISTS idx_debts_per_event_debtor_event ON debts_per_event(debtor_id, event_id);
CREATE INDEX IF NOT EXISTS idx_debts_per_event_creditor_event ON debts_per_event(creditor_id, event_id);

-- =====================================================
-- USERS TABLE INDEXES
-- =====================================================

-- Login field is already unique, but adding explicit index for clarity
-- Note: UNIQUE constraint on login already creates an index
-- CREATE INDEX IF NOT EXISTS idx_users_login ON users(login);

-- =====================================================
-- ADDITIONAL OPTIMIZATION INDEXES
-- =====================================================

-- Index for shopping list budget queries (used in debt calculations)
CREATE INDEX IF NOT EXISTS idx_shopping_lists_budget ON shopping_lists(budget) WHERE budget IS NOT NULL;

-- Index for task deadline queries (potential future feature)
CREATE INDEX IF NOT EXISTS idx_tasks_deadline ON tasks(deadline_datetime);

-- Index for event datetime queries (potential future feature)
CREATE INDEX IF NOT EXISTS idx_events_start_datetime ON events(start_datetime);
CREATE INDEX IF NOT EXISTS idx_events_end_datetime ON events(end_datetime);
