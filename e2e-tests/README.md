# E2E Тесты для Giga Chill

## Описание

Данная папка содержит End-to-End тесты для всего приложения Giga Chill, включая фронтенд и бэкенд. Тесты написаны с использованием Cypress и тестируют полные пользовательские сценарии.

## Предварительные требования

- Node.js v16+
- Запущенный фронтенд на `http://localhost:3000`. Все страницы должны быть скомпилированы перед запуском тестов, используйте `npm run build`, либо запуск через с помощью Docker.
- Запущенный бэкенд на `http://localhost:8081` с включённым тестовым профилем 
   (см. `BACKEND_PROFILE` в [.env.example](../.env.example)).

## Установка зависимостей

```bash
cd e2e-tests
npm install
```

## Запуск тестов

Перед запуском тестов можно задать переменные окружения `FRONTEND_URL` (по умолчанию `http://localhost:3000`) и `BACKEND_URL` (по умолчанию `http://localhost:8081`).

### Все тесты (автоматический режим)
```bash
npm test
```

Или с заданными переменными окружения в PowerShell (если нужно указать нестандартные адреса):
```powershell
$env:FRONTEND_URL="http://localhost:5000"; $env:BACKEND_URL="http://localhost:5001"; npm test
```

### Отдельные тесты
```bash
npm run test:specific "cypress/e2e/user-flow.cy.ts"    # Полный сценарий
```

### Интерактивный режим (GUI)
```bash
npm run test:open
```

### Запуск с видимым браузером
```bash
npm run test:headed
```

## Запуск тестов в Docker

Фронтенд и бэкенд поднимаются отдельно. Их адреса указываются в `.env` файле (см. [`.env.example`](.env.example)).

```bash
docker compose run --rm --build e2e-tests
```

Логи, скриншоты и видео сохраняются в директориях `cypress/logs`, 
`cypress/screenshots` и `cypress/videos` соответственно.

> Тесты запускаются с `network_mode: host`, который на Windows 
> поддерживается ограниченно. Например, если бэкенд или фронтенд будут 
> запущены не через Docker, то тесты не смогут к ним подключиться.
