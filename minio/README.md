# S3 setup

## All in one Docker Compose

Запуск и настройка MinIO. После запуска консоль будет доступна
по адресу [http://localhost:9001](http://localhost:9001), эндпоинт будет доступен
по адресу [http://localhost:9000](http://localhost:9000).
Можно настроить в `.env` файле (см. [`.env.example`](.env.example)).

```bash
cp .env.example .env
docker network create gigachill-network
docker compose up -d --build
```

MinIO запускается в Docker сети `gigachill-network`,
она создаётся автоматически при запуске приложения через Docker Compose в корне репозитория.

## MinIO in Docker

Запуск MinIO в Docker без настройки.

### Linux

```bash
docker run -d -p 9000:9000 -p 9001:9001 \
  -e MINIO_API_CORS_ALLOW_ORIGIN=http://localhost:3000 \
  -e MINIO_ROOT_USER=minioadmin -e MINIO_ROOT_PASSWORD=minioadmin \
  -v "./data:/data" --name gigachill-minio \
  quay.io/minio/minio server /data --console-address ":9001"
```

### Windows (PowerShell)

```powershell
docker run -d -p 9000:9000 -p 9001:9001 `
  -e MINIO_API_CORS_ALLOW_ORIGIN=http://localhost:3000 `
  -e MINIO_ROOT_USER=minioadmin -e MINIO_ROOT_PASSWORD=minioadmin `
  -v "$PWD\data:/data" --name gigachill-minio `
  quay.io/minio/minio server /data --console-address ":9001"
```

## Bootstrap MinIO

Скрипт [`bootstrap_minio.sh`](bootstrap_minio.sh) создаёт бакеты с нужными ILM-политиками
и пользователя с нужными правами. Предварительно нужно установить [`mc`](https://github.com/minio/mc?tab=readme-ov-file#gnulinux).

Этот же скрипт автоматически выполняется при запуске MinIO через Docker Compose.

```bash
# В .env.local можно указать параметры подключения к MinIO
cp .env.local.example .env.local

./bootstrap_minio.sh
```

Проверка результатов настройки MinIO.

```bash
source .env.local

# Проверка подключения к MinIO
mc admin info ${MINIO_ALIAS}

# Проверка бакетов
mc ls ${MINIO_ALIAS}

# Проверка ILM-политик бакета
mc ilm ls ${MINIO_ALIAS}/${BUCKET_INCOMING}

# Проверка списка политик
mc admin policy list ${MINIO_ALIAS}
mc admin policy info ${MINIO_ALIAS} ${SVC_USER}-policy

# Проверка информации о пользователе
mc admin user info ${MINIO_ALIAS} ${SVC_USER}
```