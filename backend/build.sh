#!/bin/bash
# Подразумевается, что скрипт используется в Docker контейнере с нужной версией gradle

set -Eeuo pipefail

# === Проверка переменной окружения KEEP_DATABASE ===
if [ -z "$KEEP_DATABASE" ]; then
    echo "Переменная окружения KEEP_DATABASE не установлена. Используйте KEEP_DATABASE=true или KEEP_DATABASE=false в .env файле."
    exit 1
fi

# === Выполнение миграций ===
./migrate.sh

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