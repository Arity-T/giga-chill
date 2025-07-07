#!/bin/bash

# === Загрузка переменных из .env ===
set -o allexport
source .env
set +o allexport

# Проверка
echo "Using DB: $DB_HOST:$DB_PORT/$DB_NAME as $DB_USER"

# Удалим и создадим базу заново (если нужно)
PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -U "$DB_USER" -c "DROP DATABASE IF EXISTS $DB_NAME"
PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -U "$DB_USER" -c "CREATE DATABASE $DB_NAME"

# Применим миграции
for file in $(find src/main/resources/db/migration -name "V*.sql" | sort); do
    echo "Applying: $file"
    PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$file" -v ON_ERROR_STOP=1
    if [ $? -ne 0 ]; then
        echo "Migration failed: $file"
        exit 1
    fi
done

echo "All migrations applied successfully!"
