# === Build Stage ===
FROM gradle:jdk21-ubi

# Установка клиента PostgreSQL
RUN microdnf update -y && \
    microdnf install -y postgresql && \
    microdnf clean all

WORKDIR /app
COPY . .

ENV OPEN_API_MAIN_SPECIFICATION=/app/openapi/build/combined.yml
COPY --from=openapi build/combined.yml /app/openapi/build/combined.yml

RUN chmod +x ./build.sh

CMD ["./build.sh"]