# Логирование

В проекте используется стек **Logback + Filebeat + Logstash + Elasticsearch + Kibana (ELK)** для сбора, парсинга, хранения и визуализации логов.

Логирование происходит следующим образом:
1. **Spring Boot** пишет логи в файл `logs/app-log.log` в **JSON-формате** с помощью **Logback**.
2. **Filebeat** отслеживает этот `.log` файл и отправляет логи в **Logstash**.
3. **Logstash** парсит и очищает сообщения (например, удаляет цветовые ANSI-коды), а затем отправляет данные в **Elasticsearch**.
4. **Kibana** визуализирует логи через поисковый интерфейс.

## Конфигурация

| Компонент | Назначение |
| --- | --- |
| `backend/src/main/resources/logback-spring.xml` | Настройка Logback, формат логов, путь до файла (`logs/app-log.log`). |
| `filebeat/filebeat.yml` | Filebeat отслеживает `logs/app-log.log`. |
| `logstash/logstash.conf` | Настройка Logstash: откуда читать логи и как отправлять в Elasticsearch. |
| `elk-stack/docker-compose.yml` | Поднимает сервисы `elasticsearch`, `kibana`, `logstash`, `filebeat`, `metricbeat`. |

## Настройка Kibana

1. Запустите ELK-стек:
  
    ```sh
    docker compose up -d --build
    ```
  
2. Перейдите в Kibana: `http://localhost:5601` (порт указывается в переменной `KIBANA_HOST_PORT`). Пользователь `elastic`, пароль - в переменной `KIBANA_PASSWORD`.

3. В левом меню откройте:
  `Management -> Stack Management -> Kibana -> Data Views`.

4. Нажмите **Create data view**, введите:
   - **Name:** `backend-logs`
   - **Index pattern:** `backend-logs-*`
   - **Timestamp field:** `@timestamp`
   - Сохраните.

5. Перейдите в **Analytics -> Discover**, выберите созданное Data View и начинайте просмотр логов.

Кроме  того, в разделе **Analytics -> Dashboards** доступен набор стандартных дашбордов от metricbeat.

## Отдельный запуск ELK-стека

Если вы хотите запустить только ELK без backend и frontend приложений:

1. Создайте внешний том для логов:
  
    ```sh
    docker volume create shared-app-logs
    ```

2. Создайте внешнюю сеть для связи контейнеров:

    ```sh
    docker network create gigachill-network
    ```

3. Перейдите в папку `elk-stack/` и запустите стек:
  
    ```sh
    docker compose up -d --build
    ```

> Убедитесь, что путь к логу **logs/app-log.log** совпадает с ожидаемым в filebeat.yml. В противном случае Filebeat не увидит лог-файл.