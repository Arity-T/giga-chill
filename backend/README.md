# Бэкенд для Gigachill

## Сборка и запуск

Перед сборкой и запуском бэкенда нужно развернуть PostreSQL. Параметры подключения к базе данных и прочие настройки указываем в файле `.env` (см. [`.env.example`](.env.example)).

Для упрощения сборки и запуска приложения написан скрипт [`dev-setup.sh`](dev-setup.sh):

```bash
# На Windows команды можно выполнить через Git Bash
# Выполнить миграции, сгенерировать Jooq и OpenAPI классы, собрать приложение и запустить
./dev-setup.sh -mgb

# Тоже самое, но без запуска приложения
./dev-setup.sh -mgbS

# Только запуск приложения
./dev-setup.sh
```

**Доступные флаги:**
- `-m` — выполнить миграции базы данных
- `-g` — сгенерировать классы Jooq и OpenAPI
- `-b` — собрать приложение
- `-S` — не запускать приложение (только выполнить указанные операции)
- `-h` — показать справку

## Сборка в Fat Jar в Docker

Собрать приложение в Docker контейнере можно с помощью [`compose.build.yml`](compose.build.yml):

```bash
docker compose -f ./compose.build.yml up --build --abort-on-container-exit --exit-code-from backend-builder
```

После успешной сборки Fat Jar будет сохранён в `./backend-build`, перед сборкой можно задать переменную окружения `BACKEND_BUILD_DIR` для указания другого пути.

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