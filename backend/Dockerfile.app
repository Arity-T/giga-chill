# === Runtime Stage ===
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
RUN apt-get update && apt-get install -y postgresql-client

COPY backend/.env .env
COPY backend/entrypoint.sh .

RUN chmod +x entrypoint.sh

EXPOSE 8081
ENTRYPOINT ["./entrypoint.sh"]
