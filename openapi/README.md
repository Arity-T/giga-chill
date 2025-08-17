# OpenAPI Specification

Для редактирования и просмотра спецификации можно использовать [Swagger Editor](https://editor.swagger.io/) (может потребоваться VPN), либо VS Code с расширением [OpenAPI (Swagger) Editor](https://marketplace.cursorapi.com/items?itemName=42Crunch.vscode-openapi).

## Mock Server with Prism

Можно запустить простой моковый сервер с помощью [Prism](https://github.com/stoplightio/prism).

```powershell
prism mock build/openapi.yml --port 8081
```

## Сборка спецификации

Для сборки спецификации API в единый файл [`openapi.yml`](build/openapi.yml) можно использовать следующие команды:

```sh
npm install
npm run build
```

## Host Swagger UI with Docker

[Параметры конфигурации](https://github.com/swagger-api/swagger-ui/blob/HEAD/docs/usage/configuration.md)


```bash
docker run -p 1240:8080 -e SWAGGER_JSON=/spec/openapi.yml -e LAYOUT=BaseLayout \
  -v /var/www/giga-chill/openapi.yml:/spec/openapi.yml \
  --name giga-chill-swagger-ui -d docker.swagger.io/swaggerapi/swagger-ui:v5.27.1
```

## Host Redocly with Docker

[Документация](https://redocly.com/docs/redoc/deployment/docker)

```bash
docker run -p 1241:80 -e SPEC_URL=openapi.yml -e PAGE_TITLE="GigaChill API — ReDoc" \
  -v /var/www/giga-chill/openapi.yml:/usr/share/nginx/html/openapi.yml \
  --name giga-chill-redoc -d redocly/redoc:v2.5.0
```