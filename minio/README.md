# S3 setup

## MinIO in Docker

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

```bash
# В .env можно указать параметры подключения к MinIO
cp .env.example .env

./bootstrap_minio.sh
```

Проверка результатов настройки MinIO.

```bash
source .env

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