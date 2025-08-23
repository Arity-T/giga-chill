-- Расширения

CREATE EXTENSION IF NOT EXISTS pgcrypto; -- Для UUID

-- Перечисления

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'event_role') THEN
    CREATE TYPE event_role AS ENUM ('participant', 'admin', 'owner');
  END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'task_status') THEN
    CREATE TYPE task_status AS ENUM ('open', 'in_progress', 'under_review', 'completed', 'canceled');
  END IF;
END$$;

-- Таблицы

CREATE TABLE IF NOT EXISTS users (
  user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  login VARCHAR(16) NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  name VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
  event_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(50) NOT NULL,
  location VARCHAR(50) NOT NULL,
  description VARCHAR(500) DEFAULT NULL,
  start_datetime TIMESTAMP WITH TIME ZONE DEFAULT NULL,
  end_datetime TIMESTAMP WITH TIME ZONE DEFAULT NULL,
  budget NUMERIC(12, 2) DEFAULT NULL,
  invite_token UUID DEFAULT gen_random_uuid(),
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  is_finalized BOOLEAN NOT NULL DEFAULT FALSE,
  finalized_at TIMESTAMP WITH TIME ZONE DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS user_in_event (
  user_id UUID NOT NULL REFERENCES users(user_id),
  event_id UUID NOT NULL REFERENCES events(event_id),
  role event_role NOT NULL DEFAULT 'participant',
  balance NUMERIC(12, 2) DEFAULT NULL,
  PRIMARY KEY (user_id, event_id)
);

CREATE TABLE IF NOT EXISTS tasks (
  task_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  event_id UUID NOT NULL REFERENCES events(event_id), 
  author_id UUID NOT NULL REFERENCES users(user_id),
  executor_id UUID DEFAULT NULL REFERENCES users(user_id),
  title VARCHAR(50) NOT NULL,
  description VARCHAR(500) DEFAULT NULL,
  status task_status NOT NULL DEFAULT 'open',
  deadline_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
  executor_comment VARCHAR(500) DEFAULT NULL,
  reviewer_comment VARCHAR(500) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS shopping_lists (
  shopping_list_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  task_id UUID DEFAULT NULL REFERENCES tasks(task_id) ON DELETE SET NULL,
  event_id UUID NOT NULL REFERENCES events(event_id),
  title VARCHAR(50) NOT NULL,
  description VARCHAR(500) DEFAULT NULL,
  file_link TEXT DEFAULT NULL,
  budget NUMERIC(12, 2) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS shopping_items (
  shopping_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  shopping_list_id UUID NOT NULL REFERENCES shopping_lists(shopping_list_id) ON DELETE CASCADE,
  title VARCHAR(50) NOT NULL,
  quantity NUMERIC(5, 2) NOT NULL,
  unit VARCHAR(20) NOT NULL,
  is_purchased BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS consumer_in_list (
  user_id UUID NOT NULL REFERENCES users(user_id),
  shopping_list_id UUID NOT NULL REFERENCES shopping_lists(shopping_list_id) ON DELETE CASCADE,
  PRIMARY KEY (user_id, shopping_list_id)
);

CREATE MATERIALIZED VIEW IF NOT EXISTS debts_per_event AS
SELECT
  sl.event_id,
  c.user_id AS debtor_id,
  t.executor_id AS creditor_id,
  ROUND(SUM(sl.budget / consumer_count.total_consumers), 2) AS amount
FROM shopping_lists sl
JOIN tasks t ON sl.task_id = t.task_id
JOIN consumer_in_list c ON c.shopping_list_id = sl.shopping_list_id
JOIN (
  SELECT 
    shopping_list_id,
    COUNT(*) as total_consumers
  FROM consumer_in_list
  GROUP BY shopping_list_id
) consumer_count ON consumer_count.shopping_list_id = sl.shopping_list_id
WHERE sl.budget IS NOT NULL
  AND t.executor_id IS NOT NULL
  AND t.status = 'completed'
GROUP BY sl.event_id, c.user_id, t.executor_id;