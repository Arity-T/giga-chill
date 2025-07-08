-- user_in_event
CREATE INDEX IF NOT EXISTS idx_user_in_event_user_id ON user_in_event(user_id);
CREATE INDEX IF NOT EXISTS idx_user_in_event_event_id ON user_in_event(event_id);

-- task
CREATE INDEX IF NOT EXISTS idx_task_event_id ON task(event_id);
CREATE INDEX IF NOT EXISTS idx_task_author_id ON task(author_id);
CREATE INDEX IF NOT EXISTS idx_task_executor_id ON task(executor_id);
CREATE INDEX IF NOT EXISTS idx_task_actual_approval_id ON task(actual_approval_id);

-- task_approval
CREATE INDEX IF NOT EXISTS idx_task_approval_task_id ON task_approval(task_id);

-- purchase_list
CREATE INDEX IF NOT EXISTS idx_purchase_list_task_id ON purchase_list(task_id);
CREATE INDEX IF NOT EXISTS idx_purchase_list_event_id ON purchase_list(event_id);

-- purchase
CREATE INDEX IF NOT EXISTS idx_purchase_purchase_list_id ON purchase(purchase_list_id);

-- consumer_in_list
CREATE INDEX IF NOT EXISTS idx_consumer_user_id ON consumer_in_list(user_id);
CREATE INDEX IF NOT EXISTS idx_consumer_purchase_list_id ON consumer_in_list(purchase_list_id);

-- purchase_list_approval
CREATE INDEX IF NOT EXISTS idx_pla_task_approval_id ON purchase_list_approval(task_approval_id);
CREATE INDEX IF NOT EXISTS idx_pla_purchase_list_id ON purchase_list_approval(purchase_list_id);
