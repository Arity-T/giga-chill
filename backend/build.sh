#!/bin/bash
# Подразумевается, что скрипт используется в Docker контейнере с нужной версией gradle
# === Проверка переменной окружения KEEP_DATABASE ===
if [ -z "$KEEP_DATABASE" ]; then
    echo "Переменная окружения KEEP_DATABASE не установлена. Используйте KEEP_DATABASE=true или KEEP_DATABASE=false в .env файле."
    exit 1
fi

# === Выполнение миграций ===
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

# === Генерация Jooq ===
echo "=== Генерация классов Jooq ==="
gradle generateJooq --no-daemon
if [ $? -ne 0 ]; then
    echo "Ошибка при генерации Jooq"
    exit 1
fi
echo "Классы Jooq сгенерированы успешно!"

# === Сборка приложения ===
echo "=== Сборка приложения ==="
gradle bootJar --no-daemon
if [ $? -ne 0 ]; then
    echo "Ошибка при сборке приложения"
    exit 1
fi
echo "Приложение собрано успешно!"

# Сохраняем JAR для последующего копирования
mkdir -p /app/build/libs
# bootJar создаёт обычный jar, по мимо fatJar, если его не удалить, то под маску подойдут два файла
rm -f build/libs/*plain.jar
mv build/libs/gigachill-*.jar /app/build/libs/app.jar