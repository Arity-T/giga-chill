-- user_in_event
CREATE INDEX IF NOT EXISTS idx_user_in_event_user_id ON user_in_event(user_id);
CREATE INDEX IF NOT EXISTS idx_user_in_event_event_id ON user_in_event(event_id);

-- tasks
CREATE INDEX IF NOT EXISTS idx_task_event_id ON tasks(event_id);
CREATE INDEX IF NOT EXISTS idx_task_author_id ON tasks(author_id);
CREATE INDEX IF NOT EXISTS idx_task_executor_id ON tasks(executor_id);
CREATE INDEX IF NOT EXISTS idx_task_actual_approval_id ON tasks(actual_approval_id);

-- task_approvals
CREATE INDEX IF NOT EXISTS idx_task_approval_task_id ON task_approvals(task_id);

-- shopping_lists
CREATE INDEX IF NOT EXISTS idx_shopping_list_task_id ON shopping_lists(task_id);
CREATE INDEX IF NOT EXISTS idx_shopping_list_event_id ON shopping_lists(event_id);

-- shopping_items
CREATE INDEX IF NOT EXISTS idx_shopping_item_shopping_list_id ON shopping_items(shopping_list_id);

-- consumer_in_list
CREATE INDEX IF NOT EXISTS idx_consumer_user_id ON consumer_in_list(user_id);
CREATE INDEX IF NOT EXISTS idx_consumer_shopping_list_id ON consumer_in_list(shopping_list_id);

-- shopping_list_approvals
CREATE INDEX IF NOT EXISTS idx_sla_task_approval_id ON shopping_list_approvals(task_approval_id);
CREATE INDEX IF NOT EXISTS idx_sla_shopping_list_id ON shopping_list_approvals(shopping_list_id);
