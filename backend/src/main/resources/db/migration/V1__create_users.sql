CREATE EXTENSION IF NOT EXISTS pgcrypto;

create table users (
    user_id UUID PRIMARY KEY default gen_random_uuid(),
    login VARCHAR(32) NOT NULL unique,
    password_hash TEXT NOT NULL,
    name VARCHAR(64) NOT NULL
);
