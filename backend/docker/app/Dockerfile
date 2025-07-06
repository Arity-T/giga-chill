FROM eclipse-temurin:21-jdk

# Установка PostgreSQL client и netcat
RUN apt-get update && \
    apt-get install -y postgresql-client netcat-openbsd && \
    rm -rf /var/lib/apt/lists/*

# Рабочая директория
WORKDIR /app

# Копируем скрипты
COPY scripts /app/scripts
RUN chmod +x /app/scripts/*.sh

# Копируем исходный код
COPY . .

# Сборка приложения
RUN ./gradlew build --no-daemon

EXPOSE 8081