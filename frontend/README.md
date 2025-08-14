# Фронтенд для Gigachill

## Сборка и запуск в Docker

Во время сборки фронтенду должна быть доступна [схема API](../openapi/api.yml)
для кодогенерации. Путь до неё указывается в [именованном контексте](https://docs.docker.com/build/concepts/context/#named-contexts) `openapi`. 
Также при сборке нужно указать два аргумента:
- `NEXT_PUBLIC_API_BASE_URL` - ссылка на API
- `NEXT_PUBLIC_BASE_URL` - ссылка на сам фронтенд

```bash
# Сборка образа
docker build -t giga-chill-frontend --build-arg NEXT_PUBLIC_API_BASE_URL=http://localhost:8081 --build-arg NEXT_PUBLIC_BASE_URL=http://localhost:3000 --build-context openapi=../openapi .

# Запуск контейнера
docker run -p 3000:3000 giga-chill-frontend
```

Либо с помощью [docker-compose](docker-compose.yml):

```bash
# Нужно указать аргументы сборки в переменных окружения
cp .env.example .env

docker-compose up -d --build
```

## Сборка и запуск (без Docker)

На компьютере должен быть установлен [Node.js](https://nodejs.org).

Создаём `.env.local` (см. [`.env.example`](.env.example)):
```bash
cp .env.example .env.local
```

Устанавливаем зависимости и генерируем код API клиента (для кодогенерации используется [спецификация OpenAPI](../openapi/api.yml)):
```bash
npm run codegen
npm install
```

Запуск сервера разработки:

```bash
# По умолчанию запускается на localhost:3000
npm run dev
```

Для запуска на другом порту (_powershell_):

```powershell
$env:PORT=3001; npm run dev
```

Если не нужно изменять код, а, например, запустить e2e тесты, то лучше сразу собрать приложение целиком. Работать будет значительно быстрее:

```bash
npm run build
npm run start
```

Для запуска на другом порту (powershell):

```powershell
$env:PORT=3001; npm run start
```
