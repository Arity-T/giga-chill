-- Расширения

CREATE EXTENSION IF NOT EXISTS pgcrypto; -- Для UUID

-- Перечисления

CREATE TYPE event_role AS ENUM ('participant', 'admin', 'owner');

CREATE TYPE task_status AS ENUM ('open', 'in_progress', 'under_review', 'completed', 'canceled');

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
  invite_link UUID DEFAULT gen_random_uuid(),
  is_deleted BOOLEAN DEFAULT FALSE
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
  deadline_datetime TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS task_approvals (
  task_approval_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  task_id UUID NOT NULL REFERENCES tasks(task_id) ON DELETE CASCADE, 
  executor_comment VARCHAR(500) DEFAULT NULL,
  reviewer_comment VARCHAR(500) DEFAULT NULL
);

-- Отдельно добавляем цикличную связь 1-к-1
ALTER TABLE tasks
ADD COLUMN actual_approval_id UUID DEFAULT NULL REFERENCES task_approvals(task_approval_id) ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS shopping_lists (
  shopping_list_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  task_id UUID DEFAULT NULL REFERENCES tasks(task_id) ON DELETE SET NULL,
  event_id UUID NOT NULL REFERENCES events(event_id),
  title VARCHAR(50) NOT NULL,
  description VARCHAR(500) DEFAULT NULL
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

CREATE TABLE IF NOT EXISTS shopping_list_approvals (
  shopping_list_approval_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  task_approval_id UUID NOT NULL REFERENCES task_approvals(task_approval_id) ON DELETE CASCADE,
  shopping_list_id UUID NOT NULL REFERENCES shopping_lists(shopping_list_id) ON DELETE CASCADE,
  budget NUMERIC(12, 2) NOT NULL,
  file_link TEXT NOT NULL
);
