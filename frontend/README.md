Это проект [Next.js](https://nextjs.org), созданный с помощью [`create-next-app`](https://nextjs.org/docs/app/api-reference/cli/create-next-app).

## Начало работы

### Переменные окружения

Перед запуском проекта настройте переменные окружения:

1. Скопируйте файл с примером переменных окружения:
   ```bash
   cp .env.example .env.local
   ```

2. Обновите переменные в `.env.local` по необходимости:
   - `NEXT_PUBLIC_API_BASE_URL` - URL бэкенд API

### Установка зависимостей и генерация кода

```bash
npm install
npm run codegen
```

### Development Server

Сначала запустите сервер разработки:

```bash
npm run dev
```

Откройте [http://localhost:3000](http://localhost:3000) в браузере, чтобы увидеть результат.

Для запуска на другом порту (powershell):

```powershell
$env:PORT=3001; npm run dev
```

### Сборка

Если не нужно изменять код, а, например, лишь запустить e2e тесты:

```bash
npm run build
npm run start
```

Для запуска на другом порту (powershell):

```powershell
$env:PORT=3001; npm run start
```

## Docker

Запускайте docker build из корневой директории проекта, фронтенду нужно видеть 
[схему API](../openapi/api.yml) для кодогенерации. Также при сборке 
нужно указать два аргумента `NEXT_PUBLIC_API_BASE_URL` и `NEXT_PUBLIC_BASE_URL`:

```bash
# Сборка образа
docker build -f frontend/Dockerfile -t giga-chill-frontend --build-arg NEXT_PUBLIC_API_BASE_URL=http://localhost:8081 --build-arg NEXT_PUBLIC_BASE_URL=http://localhost:3000 .

# Запуск контейнера
docker run -p 3000:3000 giga-chill-frontend
```