#!/bin/bash
# Применяет миграции из src/main/resources/db/migration с помощью psql
echo "=== Выполнение миграций ==="

if [ "$KEEP_DATABASE" = "false" ]; then
    echo "Пересоздание базы данных..."
    PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -U "$DB_USER" -c "DROP DATABASE IF EXISTS $DB_NAME"
    PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -U "$DB_USER" -c "CREATE DATABASE $DB_NAME"
else
    echo "Сохраняем существующую базу данных (KEEP_DATABASE=$KEEP_DATABASE)"
fi

# Применим миграции
echo "Применение миграций..."
for file in $(find src/main/resources/db/migration -name "V*.sql" | sort); do
    echo "Applying: $file"
    PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$file" -v ON_ERROR_STOP=1
    if [ $? -ne 0 ]; then
        echo "Migration failed: $file"
        exit 1
    fi
done

echo "Все миграции применены успешно!"