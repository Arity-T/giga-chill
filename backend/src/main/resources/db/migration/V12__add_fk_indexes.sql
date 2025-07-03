CREATE INDEX IF NOT EXISTS idx_user_in_event_user_id ON user_in_event(user_id);
CREATE INDEX IF NOT EXISTS idx_user_in_event_event_id ON user_in_event(event_id);