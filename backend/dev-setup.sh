#!/bin/bash

# === Функция для показа справки ===
show_help() {
    echo "Использование: $0 [ОПЦИИ]"
    echo ""
    echo "Опции:"
    echo "  -m    Выполнить миграции базы данных"
    echo "  -g    Сгенерировать классы Jooq, а также классы для OpenAPI"
    echo "  -b    Собрать приложение"
    echo "  -S    Не запускать приложение (только выполнить указанные операции)"
    echo "  -h    Показать эту справку"
    echo ""
    echo "Примеры:"
    echo "  $0          # Только запуск приложения"
    echo "  $0 -m       # Миграции + запуск"
    echo "  $0 -m -g -b # Полный цикл: миграции, генерация, сборка, запуск"
    echo "  $0 -mgb     # То же самое, но короче"
    echo "  $0 -mgbS    # Миграции, генерация, сборка БЕЗ запуска"
    echo "  $0 -mS      # Только миграции, без запуска"
    echo ""
    echo "Без параметров выполняется только bootRun (запуск приложения)"
    echo "Флаг -S отключает автоматический запуск приложения"
}

# === Инициализация флагов ===
run_migrations=false
generate_jooq_and_open_api=false
build_app=false
skip_startup=false

# === Обработка параметров ===
while getopts "mgbSh" opt; do
    case $opt in
        m) run_migrations=true ;;
        g) generate_jooq_and_open_api=true ;;
        b) build_app=true ;;
        S) skip_startup=true ;;
        h) show_help; exit 0 ;;
        *) echo "Неизвестная опция: -$OPTARG" >&2; show_help; exit 1 ;;
    esac
done

# === Загрузка переменных из .env ===
if [ ! -f .env ]; then
    echo "Ошибка: Файл .env не найден"
    echo "Убедитесь, что вы запускаете скрипт из папки backend/"
    exit 1
fi

set -o allexport
source .env
set +o allexport

echo "Using DB: $DB_HOST:$DB_PORT/$DB_NAME as $DB_USER"

# === Выполнение миграций ===
if [ "$run_migrations" = true ]; then
    ./migrate.sh
fi

# === Генерация Jooq ===
if [ "$generate_jooq_and_open_api" = true ]; then
    echo "=== Генерация классов Jooq и OpenAPI ==="
    ./gradlew clean
    ./gradlew generateJooq
    ./gradlew generateOpenApi
    if [ $? -ne 0 ]; then
        echo "Ошибка при генерации Jooq или OpenAPI"
        exit 1
    fi
    echo "Классы Jooq и OpenAPI сгенерированы успешно!"
fi

# === Сборка приложения ===
if [ "$build_app" = true ]; then
    echo "=== Сборка приложения ==="
    ./gradlew spotlessApply
    ./gradlew build
    if [ $? -ne 0 ]; then
        echo "Ошибка при сборке приложения"
        exit 1
    fi
    echo "Приложение собрано успешно!"
fi

# === Запуск приложения ===
if [ "$skip_startup" = false ]; then
    echo "=== Запуск приложения ==="
    ./gradlew bootRun
else
    echo "=== Запуск приложения пропущен (флаг -S) ==="
fi 