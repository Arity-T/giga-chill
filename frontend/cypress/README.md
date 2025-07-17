# Инструкция по запуску тестов Cypress

## Предварительные требования
- Node.js v16+
- Установленный фронтенд (папка `frontend`)
- Запущенный сервер разработки


## Запуск тестов Cypress

### 1. Установка зависимостей (из директории папки frontend)
```bash
npm install cypress --save-dev
```

### 2. Запуск сервера разработки
```bash
npm run dev
```

### 3. Запуск тестов
```bash
npx cypress run --reporter spec
```

## Альтернативные команды

### Интерактивный режим
```bash
npx cypress open
```

### Запуск конкретного теста
```bash
npx cypress run --spec "cypress/e2e/Test1.cy.ts" --reporter spec
```
