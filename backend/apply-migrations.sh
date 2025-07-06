#!/bin/bash

# Параметры из переменных окружения с fallback-значениями
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-gigachill}"
DB_USER="${DB_USER:-postgres}"
DB_PASSWORD="${DB_PASSWORD:-postgres}"

echo "Applying migrations to $DB_HOST:$DB_PORT/$DB_NAME as $DB_USER"

# Находим все миграции и сортируем по имени
find "src/main/resources/db/migration" -name "V*.sql" | sort | while read -r file; do
    echo "Applying: $(basename "$file")"
    PGPASSWORD="$DB_PASSWORD" psql \
        -h "$DB_HOST" \
        -p "$DB_PORT" \
        -U "$DB_USER" \
        -d "$DB_NAME" \
        -f "$file" \
        -v ON_ERROR_STOP=1
    if [ $? -ne 0 ]; then
        echo "Migration failed: $(basename "$file")"
        exit 1
    fi
done

echo "All migrations applied successfully!"