# https://hub.docker.com/r/minio/mc/tags
FROM minio/mc:RELEASE.2025-08-13T08-35-41Z-cpuv1 AS mc

FROM alpine:3.20
RUN apk add --no-cache bash ca-certificates
COPY --from=mc /usr/bin/mc /usr/local/bin/mc
RUN chmod +x /usr/local/bin/mc

WORKDIR /app
# .dockerignore ограничивает контекст только нужными файлами, поэтому можно
# спокойно копировать всё
COPY . .
RUN chmod +x bootstrap_minio.sh
ENTRYPOINT ["./bootstrap_minio.sh"]