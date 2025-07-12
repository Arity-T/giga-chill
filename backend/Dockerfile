FROM openjdk:21-jdk-slim

# Установим утилиты
RUN apt-get update && apt-get install -y \
    bash \
    curl \
    git \
    unzip \
    postgresql-client \
    && rm -rf /var/lib/apt/lists/*

# Установим Gradle
ARG GRADLE_VERSION=8.5
RUN curl -sSL https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip > gradle.zip \
    && unzip gradle.zip -d /opt \
    && ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/bin/gradle

WORKDIR /app
COPY backend/. /app/
COPY .env .env

RUN chmod +x ./entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]