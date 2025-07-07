# Тестовая реализация авторизации
## Структура и назначение
В папке **/model** хранятся DTO:
- `AuthRequest` для запроса (логин пароль).
- `AuthResponse` для ответа (токен).
- `UserInfo` для данных пользователя (имя пользователя).

В папке **/security** немного всё намешано:
- `JwtService` для создания и валидации JWT. Токен генерируется на 24 часа.
- `JwtFilter` достаёт токен из тела запроса, валидирует и подставляет пользователя.
- `InMemoryUserService` для хранения пользователей (имитации БД), чтобы проверить регистрацию.
- `SecurityConfig` для настройки Spring Security (защита + фильтр).

В папке **/controller**:
- `AuthController` для эндпоинтов.
- `GlobalExceptionHandler` для обработки ошибок.

## Запуск приложения
### Миграции
В файле `/backend/.env` указываем переменные окружения, например:
```
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=gigachill
DB_USER=postgres
DB_USER=postgres
DB_PASSWORD=postgres

# Backend server
SERVER_PORT=3000

# Jwt
JWT_EXPIRATION=1d

# Frontend server
FRONTEND_ORIGIN=*
``` 

Теперь можем запустить миграции, для этого запускаем скрипт из папки `/backend`:

```pwsh
./scripts/manual-init.sh
```

### Генерация классов
После успешного выполнения миграций можем сгенерировать классы командой

```pwsh
./gradlew clean generateJooq
```

В папке `/backend/build/generated-sources/jooq/.../tables` должны появиться классы, соответствующие таблицам БД.


### Запуск приложения

Далее собираем приложение:

```pwsh
./gradlew build
```

И теперь можем его запустить:
```pwsh
./gradlew bootRun
```

После запуска приложения можем перейти к проверке эндпоинтов.

## Проверка эндпоинтов
- **Регистрация:**
```pwsh
curl -i -X POST http://localhost:3000/auth/register -H "Content-Type: application/json" -d '{"login":"vlad", "password":"1234", "name":"Владислав Гаар"}' -c cookies.txt
```
Регистрирует пользователя. JWT-токен будет отправлен в Set-Cookie и сохранён в cookies.txt.

- **Логин:**
```pwsh
curl -i -X POST http://localhost:3000/auth/login -H "Content-Type: application/json" -d '{"login":"vlad", "password":"1234"}' -c cookies.txt
```
Логинится с уже зарегистрированным пользователем. JWT тоже сохранится в cookies.txt.

- **Эндпоинт /me:**
```pwsh
curl -X GET http://localhost:3000/me -b cookies.txt
```
Отправляет токен из куки. Возвращает информацию о текущем пользователе. Если куки нет или токен истёк — будет 401 ошибка.