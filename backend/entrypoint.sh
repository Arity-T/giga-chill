#!/bin/bash

# === Функция для показа справки ===
show_help() {
    echo "Использование: $0 [ОПЦИИ]"
    echo ""
    echo "Опции:"
    echo "  -m    Выполнить миграции базы данных"
    echo "  -g    Сгенерировать классы Jooq"
    echo "  -b    Собрать приложение"
    echo "  -k    Сохранить базу данных (не пересоздавать)"
    echo "  -S    Не запускать приложение (только выполнить указанные операции)"
    echo "  -h    Показать эту справку"
    echo ""
    echo "Примеры:"
    echo "  $0          # Только запуск приложения"
    echo "  $0 -m       # Миграции + запуск"
    echo "  $0 -m -g -b # Полный цикл: миграции, генерация, сборка, запуск"
    echo "  $0 -mgb     # То же самое, но короче"
    echo "  $0 -mgbk    # Миграции, генерация, сборка с сохранением БД"
    echo "  $0 -mgbS    # Миграции, генерация, сборка БЕЗ запуска"
    echo "  $0 -mS      # Только миграции, без запуска"
    echo ""
    echo "Без параметров выполняется только bootRun (запуск приложения)"
    echo "Флаг -S отключает автоматический запуск приложения"
}

# === Инициализация флагов ===
run_migrations=false
generate_jooq=false
build_app=false
skip_startup=false
keep_database=false

# === Обработка параметров ===
while getopts "mgbkSh" opt; do
    case $opt in
        m) run_migrations=true ;;
        g) generate_jooq=true ;;
        b) build_app=true ;;
        k) keep_database=true ;;
        S) skip_startup=true ;;
        h) show_help; exit 0 ;;
        *) echo "Неизвестная опция: -$OPTARG" >&2; show_help; exit 1 ;;
    esac
done

# === Загрузка переменных из .env ===
if [ ! -f .env ]; then
    echo "Ошибка: Файл .env не найден в текущей директории"
    echo "Убедитесь, что вы запускаете скрипт из папки backend/"
    exit 1
fi

set -o allexport
source .env
set +o allexport

echo "DEBUG: Проверка соединения: PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -c '\q'"
PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -c '\q'
echo "DEBUG: Код возврата psql: $?"

echo "Using DB: $DB_HOST:$DB_PORT/$DB_NAME as $DB_USER"

# ===Ждём, пока Postgres не станет доступен===
MAX_ATTEMPTS=20
ATTEMPT_INTERVAL=10

echo "Ожидание запуска Postgres"
attempt=1
until [ $attempt -gt $MAX_ATTEMPTS ]; do
  if PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -U "$DB_USER" -c '\q' 2>/dev/null; then
    echo "Postgres готов"
    break
  fi
  >&2 echo "Попытка $attempt из $MAX_ATTEMPTS: Postgres ещё не доступен - ждём $ATTEMPT_INTERVAL сек"
  sleep $ATTEMPT_INTERVAL
  attempt=$((attempt + 1))
done

if [ $attempt -gt $MAX_ATTEMPTS ]; then
  >&2 echo "Postgres так и не стал доступен после $MAX_ATTEMPTS попыток"
  exit 1
fi

# === Выполнение миграций ===
if [ "$run_migrations" = true ]; then
    echo "=== Выполнение миграций ==="
    
    if [ "$keep_database" = false ]; then
        # Удалим и создадим базу заново
        echo "Пересоздание базы данных..."
        PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -U "$DB_USER" -c "DROP DATABASE IF EXISTS $DB_NAME"
        PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -U "$DB_USER" -c "CREATE DATABASE $DB_NAME"
    else
        PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -U "$DB_USER" -c "CREATE DATABASE $DB_NAME"
        echo "Сохранение существующей базы данных (флаг -k)"
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
fi

# === Генерация Jooq ===
if [ "$generate_jooq" = true ]; then
    echo "=== Генерация классов Jooq ==="
    ./gradlew generateJooq
    if [ $? -ne 0 ]; then
        echo "Ошибка при генерации Jooq"
        exit 1
    fi
    echo "Классы Jooq сгенерированы успешно!"
fi

# === Сборка приложения ===
if [ "$build_app" = true ]; then
    echo "=== Сборка приложения ==="
    ./gradlew bootJar
    if [ $? -ne 0 ]; then
        echo "Ошибка при сборке приложения"
        exit 1
    fi
    echo "Приложение собрано успешно!"

    # Посмотреть, что реально есть
    ls -l build/libs

    # Сохраняем JAR для последующего копирования
    mkdir -p /app/build/libs
    cp build/libs/*.jar /app/build/libs/app.jar

    # Посмотреть, что скопировалось
    ls -l /app/build/libs
fi

# === Запуск приложения ===
if [ "$skip_startup" = false ]; then
    echo "=== Запуск приложения ==="
    java -jar /app/build/libs/app.jar
else
    echo "=== Запуск приложения пропущен (флаг -S) ==="
fi 