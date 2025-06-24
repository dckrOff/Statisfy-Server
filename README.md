# Statisfy Server

Statisfy Server - это бэкенд-сервер для мобильного приложения Statisfy, которое предоставляет пользователям ежедневные факты, статистику и новости. Проект использует Spring Boot и интегрируется с различными API, включая OpenAI и NewsAPI.

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
5. Grafana будет доступна по адресу `http://localhost:3000`
6. Prometheus будет доступен по адресу `http://localhost:9090`

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
- `GET /api/ai/generate-fact` - Генерация факта с использованием AI
- `GET /api/ai/analyze-news` - Анализ релевантности новости для пользователя

Более подробная документация API доступна по эндпоинту `/swagger-ui.html` после запуска приложения.

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

## Развертывание в production

Для production-среды рекомендуется:

1. Использовать внешнюю базу данных PostgreSQL и Redis с соответствующей настройкой резервного копирования
2. Настроить HTTPS с использованием SSL-сертификата
3. Использовать механизм оркестрации контейнеров, например Kubernetes
4. Настроить системы мониторинга и оповещения

## Лицензия

[MIT](LICENSE) 