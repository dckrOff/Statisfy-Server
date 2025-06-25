# Statisfy Server

Statisfy Server - это бэкенд-сервер для мобильного приложения Statisfy, которое предоставляет пользователям ежедневные факты, статистику и новости. Проект использует Spring Boot и интегрируется с различными API, включая OpenAI и NewsAPI.

## Статус проекта

✅ **Завершены все 8 этапов разработки**

1. ✅ Этап 1: Базовая инфраструктура и API пользователей
2. ✅ Этап 2: Система контента (факты и статистика)
3. ✅ Этап 3: Новостная система и внешние API
4. ✅ Этап 4: Базовая ИИ интеграция
5. ✅ Этап 5: Система уведомлений и планировщик
6. ✅ Этап 6: Аналитика и мониторинг
7. ✅ Этап 7: Производительность и безопасность
8. ✅ Этап 8: Финальная полировка и документация

## Основные функции

- Аутентификация и авторизация пользователей с использованием JWT
- Управление контентом (факты, статистика, новости)
- Интеграция с NewsAPI для получения актуальных новостей
- Веб-скрапинг новостных сайтов, которые не предоставляют API
- Интеграция с OpenAI для генерации персонализированного контента
- Анализ релевантности новостей для пользователей
- Кэширование контента с использованием Redis
- Ограничение скорости запросов для защиты от DoS-атак
- Мониторинг и метрики с использованием Spring Boot Actuator, Prometheus и Grafana
- Полная документация API с использованием OpenAPI/Swagger

## Технологии

- Java 17
- Spring Boot 3.x
- Spring Security с JWT
- Spring Data JPA
- PostgreSQL
- Redis для кэширования и ограничения скорости запросов
- Jsoup для веб-скрапинга
- OpenAI API для генерации контента
- NewsAPI для сбора новостей
- Docker и Docker Compose для развертывания
- Prometheus и Grafana для мониторинга
- OpenAPI/Swagger для документации API

## Архитектура

Проект построен с использованием чистой архитектуры, разделенной на следующие слои:

- **Controllers**: обработка HTTP запросов
- **Services**: бизнес-логика
- **Repositories**: доступ к данным
- **Models/Entities**: модели данных
- **DTOs**: объекты передачи данных
- **Mappers**: преобразование между DTOs и моделями
- **Configs**: конфигурация Spring Boot
- **Security**: настройка безопасности
- **Integrations**: интеграция с внешними API

## Запуск проекта

### Предварительные требования

- JDK 17 или выше
- Docker и Docker Compose

### Запуск с использованием Docker Compose

1. Клонируйте репозиторий:
   ```bash
   git clone <repository-url>
   cd statisfy-server
   ```

2. Создайте файл `.env` в корне проекта со следующими переменными окружения:
   ```
   # Database
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=your_secure_password
   POSTGRES_DB=statisfy

   # Redis
   REDIS_PORT=6379

   # Application
   APP_PORT=8080
   
   # API Keys
   NEWS_API_KEY=your_news_api_key
   OPENAI_API_KEY=your_openai_api_key
   
   # Security
   JWT_SECRET=your_secure_jwt_secret
   
   # Monitoring
   PROMETHEUS_PORT=9090
   GRAFANA_PORT=3000
   GRAFANA_ADMIN_USER=admin
   GRAFANA_ADMIN_PASSWORD=your_secure_password
   ```

3. Запустите приложение с помощью Docker Compose:
   ```bash
   docker-compose up -d
   ```

4. Приложение будет доступно по адресу `http://localhost:8080`
5. Swagger UI будет доступен по адресу `http://localhost:8080/swagger-ui/index.html`
6. Grafana будет доступна по адресу `http://localhost:3000`
7. Prometheus будет доступен по адресу `http://localhost:9090`

### Запуск с помощью Gradle

1. Настройте локальную базу данных PostgreSQL и Redis
2. Обновите `application.yml` или используйте переменные окружения для настройки подключения
3. Запустите приложение:
   ```bash
   ./gradlew bootRun
   ```

## API Endpoints

Основные API endpoints:

- `POST /api/auth/register` - Регистрация нового пользователя
- `POST /api/auth/login` - Аутентификация пользователя
- `GET /api/facts` - Получение списка фактов
- `GET /api/statistics` - Получение списка статистических данных
- `GET /api/news` - Получение списка новостей
- `POST /api/ai/generate-fact` - Генерация факта с использованием AI
- `POST /api/ai/analyze-news` - Анализ релевантности новости для пользователя

Полная документация API доступна по эндпоинту `/swagger-ui/index.html` после запуска приложения.

## Мониторинг

Проект включает в себя полный стек мониторинга:

- Spring Boot Actuator для сбора метрик
- Prometheus для хранения временных рядов
- Grafana для визуализации метрик

Основные метрики включают:
- Количество запросов к API
- Количество просмотров контента по типам
- Время выполнения запросов
- Использование памяти JVM
- Активные сессии пользователей
- Количество превышений лимита запросов

## Production Ready

Проект полностью готов к развертыванию в production среде:

- ✅ Graceful shutdown для корректного завершения работы
- ✅ Health checks для проверки состояния приложения
- ✅ Metrics endpoints для мониторинга
- ✅ Полная документация API с использованием OpenAPI/Swagger
- ✅ Тесты для критического функционала
- ✅ Docker и Docker Compose для развертывания
- ✅ Настройки безопасности и защиты от атак

## Документация

Подробная документация проекта доступна в файле [DOCUMENTATION.md](DOCUMENTATION.md).

## Лицензия

[MIT](LICENSE)

## Автоматический сбор данных

Проект включает в себя функциональность для автоматического сбора данных из различных внешних источников:

### Сбор статистики

Реализована система автоматического сбора статистических данных из следующих источников:
- World Bank API (данные о населении, здравоохранении, образовании, экологии)
- Open Exchange Rates API (курсы валют)

Сбор статистики выполняется по расписанию:
- Полный сбор статистики - ежедневно в 01:00
- Экономическая статистика (курсы валют) - каждые 12 часов

API для управления сбором статистики (требуется роль ADMIN):
- `POST /api/statistics-collector/run-all` - Запуск сбора всех видов статистики
- `POST /api/statistics-collector/population` - Сбор статистики о населении
- `POST /api/statistics-collector/economic` - Сбор экономической статистики
- `POST /api/statistics-collector/health` - Сбор статистики о здравоохранении
- `POST /api/statistics-collector/education` - Сбор статистики об образовании
- `POST /api/statistics-collector/environment` - Сбор статистики об экологии

### Сбор фактов

Реализована система автоматического сбора интересных фактов из следующих источников:
- Wikipedia API (общие знания)
- Numbers API (факты о числах)
- History API (исторические события)
- Space News API (научные факты)

Сбор фактов выполняется по расписанию:
- Полный сбор фактов - ежедневно в 02:00
- Научные факты - каждые 2 дня в 04:00
- Исторические факты - каждый понедельник в 03:00

API для управления сбором фактов (требуется роль ADMIN):
- `POST /api/fact-collector/run-all` - Запуск сбора всех видов фактов
- `POST /api/fact-collector/wikipedia` - Сбор фактов из Wikipedia
- `POST /api/fact-collector/numbers` - Сбор фактов о числах
- `POST /api/fact-collector/historical` - Сбор исторических фактов
- `POST /api/fact-collector/science` - Сбор научных фактов

## Настройка сбора данных

Параметры сбора данных можно настроить в файле `application.yml`:

```yaml
# Настройки сбора статистики
statistics-collector:
  enabled: true
  connection-timeout: 10000
  user-agent: "Statisfy-StatisticsCollector/1.0"
  sources:
    - name: "worldbank"
      url: "http://api.worldbank.org/v2"
      category: "general"
      enabled: true
    - name: "openexchangerates"
      url: "https://openexchangerates.org/api"
      api-key: ${OPEN_EXCHANGE_RATES_API_KEY:your_key_here}
      category: "economic"
      enabled: true

# Настройки сбора фактов
fact-collector:
  enabled: true
  connection-timeout: 10000
  user-agent: "Statisfy-FactCollector/1.0"
  max-facts-per-run: 20
  sources:
    - name: "wikipedia"
      url: "https://en.wikipedia.org/api/rest_v1"
      category: "general"
      enabled: true
    - name: "numbersapi"
      url: "http://numbersapi.com"
      category: "math"
      enabled: true
``` 