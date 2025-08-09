# OpenAPI Specification

Для редактирования и просмотра спецификации можно использовать [Swagger Editor](https://editor.swagger.io/) (может потребоваться VPN), либо VS Code с расширением [OpenAPI (Swagger) Editor](https://marketplace.cursorapi.com/items?itemName=42Crunch.vscode-openapi).

## Mock Server with Prism

Можно запустить простой моковый сервер с помощью [Prism](https://github.com/stoplightio/prism).

```powershell
prism mock api.yml --port 3000
```
## Merge apis specification

Для сборки основного API, а также для слияния его с API для тестов используйте следующие скрипты:

1. Устанавливаем все необходимые зависимости для Node js:
    ```sh
    npm install
    ```
2. Выполняем основные скрипты для объединения API: 
    ```sh
    npm run build
    ```

Конечный файл имеет следующий путь: `build/combined.yml`