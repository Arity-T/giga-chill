# giga-chill

## Сборка и запуск в Docker

Создать файл `.env` в корне проекта (см. [`.env.example`](.env.example)).

```bash
cp .env.example .env
```

Собрать и запустить фронтенд, бэкенд и базу данных:

```bash
docker compose up --build -d
```

По умолчанию фронтенд приложения будет доступен по адресу [http://localhost:3000](http://localhost:3000), бэкенд [http://localhost:8081](http://localhost:8081).

Иногда может потребоваться перезапустить бэкенд без пересборки, например, при изменении настроек через переменные окружения:

```bash
docker compose up -d --no-deps backend
```