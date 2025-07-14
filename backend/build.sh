#!/bin/bash

# === Функция для показа справки ===
show_help() {
    echo "Использование: $0 [ОПЦИИ]"
    echo ""
    echo "Опции:"
    echo "  -k    Не пересоздавать БД"
    echo "  -h    Показать эту справку"
}

# === Инициализация флагов ===
keep_database=false

# === Обработка параметров ===
while getopts "kh" opt; do
    case $opt in
        k) keep_database=true ;;
        h) show_help; exit 0 ;;
        *) echo "Неизвестная опция: -$OPTARG" >&2; show_help; exit 1 ;;
    esac
done

# === Выполнение миграций ===
echo "=== Выполнение миграций ==="

if [ "$keep_database" = false ]; then
    echo "Пересоздание базы данных..."
    PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -U "$DB_USER" -c "DROP DATABASE IF EXISTS $DB_NAME"
    PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -U "$DB_USER" -c "CREATE DATABASE $DB_NAME"
else
    echo "Сохраняем существующую базу данных (флаг -k)"
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
./gradlew generateJooq
if [ $? -ne 0 ]; then
    echo "Ошибка при генерации Jooq"
    exit 1
fi
echo "Классы Jooq сгенерированы успешно!"


# === Сборка приложения ===
echo "=== Сборка приложения ==="
./gradlew bootJar
if [ $? -ne 0 ]; then
    echo "Ошибка при сборке приложения"
    exit 1
fi
echo "Приложение собрано успешно!"

# Сохраняем JAR для последующего копирования
mkdir -p /app/build/libs
mv build/libs/gigachill-*.jar /app/build/libs/app.jar