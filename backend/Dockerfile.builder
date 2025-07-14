# === Build Stage ===
FROM gradle:jdk21-ubi

# Установка клиента PostgreSQL
RUN microdnf update -y && \
    microdnf install -y postgresql && \
    microdnf clean all

WORKDIR /app
COPY backend/ .
COPY .env .

RUN chmod +x ./build.sh

# Сохраняем результаты сборки
VOLUME /app/build