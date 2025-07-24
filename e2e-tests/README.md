# E2E Тесты для Giga Chill

## Описание

Данная папка содержит End-to-End тесты для всего приложения Giga Chill, включая фронтенд и бэкенд. Тесты написаны с использованием Cypress и тестируют полные пользовательские сценарии.

## Архитектура тестов

Проект использует современную архитектуру тестирования Cypress:

### 📁 Структура файлов

```
e2e-tests/
├── cypress/
│   ├── e2e/                    # Тестовые сценарии
│   │   ├── auth.cy.ts          # Тесты аутентификации
│   │   └── user-flow.cy.ts     # Полный пользовательский сценарий
│   ├── fixtures/               # Тестовые данные
│   │   └── users.json          # Данные пользователей
│   ├── support/                # Вспомогательные функции
│   │   ├── commands/           # Custom commands по доменам
│   │   │   ├── auth.ts         # Команды аутентификации
│   │   │   ├── events.ts       # Команды мероприятий
│   │   │   ├── participants.ts # Команды участников
│   │   │   ├── shopping-lists.ts # Команды списков покупок
│   │   │   └── index.ts        # Главный файл импорта
│   │   ├── config/             # Конфигурация
│   │   │   └── pages.config.ts # Адреса страниц
│   │   ├── commands.ts         # Импорт команд
│   │   └── e2e.ts             # Конфигурация поддержки
│   └── screenshots/            # Скриншоты неудачных тестов
├── cypress.config.ts           # Конфигурация Cypress
├── package.json
└── README.md
```

### 🔧 Custom Commands

Команды организованы по разделам [спецификации API](../openapi/api.yml) для лучшей структуры и читаемости:

```
cypress/support/
├── commands/
│   ├── auth.ts              # Команды аутентификации
│   ├── events.ts            # Команды работы с мероприятиями
│   ├── participants.ts      # Команды работы с участниками
│   ├── shopping-lists.ts    # Команды работы со списками покупок
│   └── index.ts            # Главный файл импорта
└── config/
    └── pages.config.ts      # Конфигурация адресов страниц
```

#### Команды аутентификации (Auth):
- `cy.registerUserUI(name, username, password)` - регистрация пользователя
- `cy.loginUserUI(username, password)` - вход в систему

#### Команды мероприятий (Events):
- `cy.createEventUI(eventData)` - создание мероприятия

#### Команды участников (Participants):
- `cy.addParticipantByLoginUI(username)` - добавление участника по логину
- `cy.changeParticipantRoleByNameUI(name, role)` - изменение роли участника

#### Команды списков покупок (Shopping Lists):
- `cy.createShoppingListUI(listName)` - создание списка покупок
- `cy.addShoppingItemUI(listName, itemData)` - добавление товара в список

#### Особенности команд:
- Все команды имеют суффикс `UI`, чтобы отличать их от будущих API команд
- **Автоматическая навигация**: команды самостоятельно переходят на нужные страницы, если ещё не находятся на них
- **Конфигурация адресов**: используется централизованный конфиг страниц `pages.config.ts`

## Предварительные требования

- Node.js v16+
- Запущенный фронтенд на `http://localhost:3001`
- Запущенный и настроенный бэкенд

## Установка зависимостей

```bash
cd e2e-tests
npm install
```

## Запуск тестов

### Все тесты (автоматический режим)
```bash
npm test
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

## Добавление новых тестов

### 1. Создание нового теста
```typescript
describe('Новый тестовый сценарий', () => {
  let users: any;

  before(() => {
    cy.fixture('users').then((data) => {
      users = data;
    });
  });

  beforeEach(() => {
    cy.clearAllCookies();
    cy.clearAllLocalStorage();
    cy.clearAllSessionStorage();
  });

  it('Описание теста', () => {
    const user = users.testUsers[0];
    cy.registerUser(user.name, user.username, user.password);
    // Дальнейшие действия...
  });
});
```

### 2. Использование custom commands
```typescript
// Аутентификация
cy.registerUserUI('Имя', 'username', 'password');
cy.loginUserUI('username', 'password');

// Создание мероприятия
cy.createEventUI({
  title: 'Пикник',
  location: 'Лес',
  startDay: '18',
  startHour: '03',
  endDay: '23',
  endHour: '20',
  description: 'Описание мероприятия'
});

// Работа с участниками
cy.addParticipantByLoginUI('username');
cy.changeParticipantRoleByNameUI('Имя участника', 'Администратор');

// Списки покупок
cy.createShoppingListUI('Напитки');
cy.addShoppingItemUI('Напитки', {
  name: 'Сок яблочный',
  quantity: '3',
  unit: 'л'
});
```

### 3. Добавление новых fixtures
Создайте JSON файл в `cypress/fixtures/` и используйте:
```typescript
cy.fixture('filename').then((data) => {
  // Используйте данные
});
```

## Отладка

- Используйте `cypress open` для интерактивной отладки
- Проверяйте скриншоты в папке `cypress/screenshots/` при падении тестов
- Используйте `cy.pause()` для остановки выполнения в нужном месте
