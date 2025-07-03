-- Расширения

CREATE EXTENSION IF NOT EXISTS pgcrypto; -- Для UUID

-- Перечисления

CREATE TYPE event_role AS ENUM ('participant', 'admin', 'owner'); -- Обсудить названия

CREATE TYPE task_status AS ENUM ('open', 'in work', 'under review', 'completed', 'canceled'); -- Обсудить названия

-- Таблицы

CREATE TABLE IF NOT EXISTS users (
  user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  login VARCHAR(16) NOT NULL UNIQUE, -- Согласовать и прописать ограничения
  password_hash TEXT NOT NULL,
  name VARCHAR(32) NOT NULL -- Согласовать и прописать ограничения
);

CREATE TABLE IF NOT EXISTS event (
  event_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(32) NOT NULL, -- Ограничения
  location TEXT NOT NULL,
  description TEXT DEFAULT NULL,
  start_datetime TIMESTAMP DEFAULT NULL,
  end_datetime TIMESTAMP DEFAULT NULL,
  budget NUMERIC DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS user_in_event (
  user_in_event_id SERIAL PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(user_id),
  event_id UUID NOT NULL REFERENCES event(event_id),
  role event_role NOT NULL DEFAULT 'participant',
  balance NUMERIC DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS task (
  task_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  event_id UUID NOT NULL REFERENCES event(event_id), 
  author_id UUID NOT NULL REFERENCES users(user_id),
  executor_id UUID DEFAULT NULL REFERENCES users(user_id),
  title TEXT NOT NULL, -- Ограничения
  description TEXT DEFAULT NULL,
  status task_status NOT NULL DEFAULT 'open',
  deadline_datetime TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS task_approval (
  task_approval_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- UUID или хватит SERIAL (будет ли видно на эндпоинтах)?
  task_id UUID NOT NULL REFERENCES task(task_id) ON DELETE CASCADE, 
  executor_comment TEXT DEFAULT NULL,
  reviewer_comment TEXT DEFAULT NULL
);

-- Отдельно добавляем цикличную связь 1-к-1
ALTER TABLE task
ADD COLUMN actual_approval_id UUID DEFAULT NULL REFERENCES task_approval(task_approval_id);

CREATE TABLE IF NOT EXISTS purchase_list (
  purchase_list_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  task_id UUID NOT NULL REFERENCES task(task_id), -- Список же не может существовать без задачи?
  event_id UUID NOT NULL REFERENCES event(event_id),
  title TEXT NOT NULL, -- Можем сделать ещё и unique
  comment TEXT DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS purchase (
  purchase_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  purchase_list_id UUID NOT NULL REFERENCES purchase_list(purchase_list_id), -- Покупка не существует без задачи?
  title TEXT NOT NULL, -- Можно unique, можно ограничения
  quantity INTEGER NOT NULL,
  unit TEXT NOT NULL, -- Можно ограничения
  is_purchased BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS consumer_in_list (
  consumer_in_list_id SERIAL PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(user_id),
  purchase_list_id UUID NOT NULL REFERENCES purchase_list(purchase_list_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS purchase_list_approval (
  purchase_list_approval_id SERIAL PRIMARY KEY, -- UUID или хватит SERIAL (будет ли видно на эндпоинтах)?
  task_approval_id UUID NOT NULL REFERENCES task_approval(task_approval_id) ON DELETE CASCADE,
  purchase_list_id UUID NOT NULL REFERENCES purchase_list(purchase_list_id) ON DELETE CASCADE,
  budget NUMERIC NOT NULL,
  file_link TEXT NOT NULL
);
