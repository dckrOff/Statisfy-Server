# Statisfy Server - Документация проекта

## Оглавление
1. [Общая информация](#общая-информация)
2. [Архитектура](#архитектура)
3. [Технологии](#технологии)
4. [Структура проекта](#структура-проекта)
5. [API Endpoints](#api-endpoints)
6. [Модели данных](#модели-данных)
7. [Безопасность](#безопасность)
8. [Кэширование](#кэширование)
9. [Мониторинг и метрики](#мониторинг-и-метрики)
10. [Интеграции](#интеграции)
11. [Развертывание](#развертывание)
12. [Настройка](#настройка)

## Общая информация

**Statisfy Server** - серверная часть мобильного приложения для ежедневных интересных фактов, статистики и новостей. Приложение предоставляет REST API с интеграцией ИИ для персонализации контента.

### Основные возможности:
- Система пользователей с JWT аутентификацией
- CRUD операции для фактов, статистики и категорий
- Интеграция с новостными API и система сбора новостей
- Интеграция с OpenAI для генерации контента и анализа
- Система уведомлений и планировщик задач
- Аналитика и мониторинг
- Оптимизация производительности и безопасность
- Персонализированный контент на основе интересов пользователя
- Рекомендации контента на основе активности пользователя

## Архитектура

Проект построен по многоуровневой архитектуре:

```
Statisfy Server
├── Controllers (REST API)
├── Services (Business Logic)
├── Repositories (Data Access)
├── External Integrations
│   ├── OpenAI API
│   ├── News API
│   └── Firebase CM
├── Scheduled Tasks
├── Security & Auth (JWT)
├── Caching (Redis)
└── Database (PostgreSQL)
```

## Технологии

- **Backend:** Spring Boot 3.x, Java 17+
- **Database:** PostgreSQL 14+
- **Caching:** Redis
- **Security:** Spring Security, JWT
- **AI Integration:** OpenAI API
- **Notifications:** Firebase Cloud Messaging
- **Documentation:** Swagger/OpenAPI
- **Containerization:** Docker, Docker Compose
- **Monitoring:** Spring Boot Actuator, Prometheus, Grafana

## Структура проекта

```
src/main/java/uz/dckroff/statisfy/
├── config/             # Конфигурационные классы
├── controller/         # REST контроллеры
├── dto/                # Data Transfer Objects
├── model/              # Модели данных (Entity)
├── repository/         # JPA репозитории
├── service/            # Бизнес-логика
│   └── impl/           # Реализации сервисов
├── security/           # Классы безопасности и JWT
├── exception/          # Обработка исключений
├── filter/             # Фильтры запросов
├── interceptor/        # Перехватчики запросов
├── actuator/           # Кастомные health checks и метрики
├── scheduler/          # Планировщики задач
└── StatisfyApplication.java # Точка входа в приложение
```

## API Endpoints

### Аутентификация и пользователи
```
POST /api/auth/register - регистрация
POST /api/auth/login - вход
GET /api/user/profile - профиль пользователя
PUT /api/user/profile - обновление профиля
```

### Факты
```
GET /api/facts - получить факты (с пагинацией)
GET /api/facts/{id} - получить факт по ID
GET /api/facts/category/{categoryId} - получить факты по категории
GET /api/facts/recent - получить последние факты
POST /api/facts - создать факт (только админ)
PUT /api/facts/{id} - обновить факт (только админ)
DELETE /api/facts/{id} - удалить факт (только админ)
```

### Статистика
```
GET /api/statistics - получить статистику
GET /api/statistics/{id} - получить статистику по ID
POST /api/statistics - создать статистику (только админ)
PUT /api/statistics/{id} - обновить статистику (только админ)
DELETE /api/statistics/{id} - удалить статистику (только админ)
```

### Категории
```
GET /api/categories - получить категории
GET /api/categories/{id} - получить категорию по ID
POST /api/categories - создать категорию (только админ)
PUT /api/categories/{id} - обновить категорию (только админ)
DELETE /api/categories/{id} - удалить категорию (только админ)
```

### Новости
```
GET /api/news - получить новости (с фильтрами)
GET /api/news/{id} - получить новость по ID
GET /api/news/relevant - получить релевантные новости
```

### ИИ
```
POST /api/ai/generate-fact - генерация факта по теме
POST /api/ai/analyze-news - анализ релевантности новости
GET /api/ai/daily-fact - факт дня (персонализированный)
```

### Уведомления
```
POST /api/notifications/register-device - регистрация устройства
POST /api/notifications/send - отправка уведомления (админ)
GET /api/notifications/settings - настройки уведомлений
PUT /api/notifications/settings - обновление настроек
```

### Аналитика
```
GET /api/analytics/user-stats - статистика пользователя
GET /api/analytics/popular-content - популярный контент
GET /admin/analytics/dashboard - админская аналитика
```

### Документация API
```
GET /swagger-ui/index.html - Swagger UI
GET /v3/api-docs - OpenAPI JSON
```

### Мониторинг (Actuator)
```
GET /actuator/health - проверка здоровья
GET /actuator/metrics - метрики
GET /actuator/prometheus - метрики для Prometheus
```

### Новые API
```
GET     | /api/admin/analytics/user-activity| Активность пользователей           | ADMIN          |
```

### API для сбора данных

| Метод   | Эндпоинт                          | Описание                           | Требуемая роль |
|---------|-----------------------------------|-----------------------------------|----------------|
| POST    | /api/statistics-collector/run-all | Запуск сбора всех видов статистики | ADMIN          |
| POST    | /api/statistics-collector/population | Сбор статистики о населении     | ADMIN          |
| POST    | /api/statistics-collector/economic | Сбор экономической статистики     | ADMIN          |
| POST    | /api/statistics-collector/health  | Сбор статистики о здравоохранении  | ADMIN          |
| POST    | /api/statistics-collector/education | Сбор статистики об образовании   | ADMIN          |
| POST    | /api/statistics-collector/environment | Сбор статистики об экологии    | ADMIN          |
| POST    | /api/fact-collector/run-all       | Запуск сбора всех видов фактов     | ADMIN          |
| POST    | /api/fact-collector/wikipedia     | Сбор фактов из Wikipedia           | ADMIN          |
| POST    | /api/fact-collector/numbers       | Сбор фактов о числах               | ADMIN          |
| POST    | /api/fact-collector/historical    | Сбор исторических фактов           | ADMIN          |
| POST    | /api/fact-collector/science       | Сбор научных фактов                | ADMIN          |

#### Пример ответа при сборе статистики

```json
{
  "statisticsCollected": 25,
  "source": "World Bank API",
  "category": "Население",
  "success": true,
  "message": "Успешно собрано 25 записей о населении"
}
```

#### Пример ответа при сборе фактов

```json
{
  "factsCollected": 10,
  "source": "Wikipedia",
  "category": "Общие знания",
  "success": true,
  "message": "Успешно собрано 10 фактов из Wikipedia"
}
```

## Модели данных

### User
- id (UUID)
- username (String)
- email (String)
- password (String)
- role (Enum: USER, ADMIN)
- createdAt (LocalDateTime)

### Fact
- id (UUID)
- title (String)
- content (String)
- category (Category)
- source (String)
- isPublished (Boolean)
- createdAt (LocalDateTime)

### Statistic
- id (UUID)
- title (String)
- value (Double)
- unit (String)
- category (Category)
- source (String)
- date (LocalDate)

### Category
- id (UUID)
- name (String)
- description (String)

### News
- id (UUID)
- title (String)
- summary (String)
- url (String)
- source (String)
- publishedAt (LocalDateTime)
- category (Category)
- isRelevant (Boolean)

### UserPreference
- id (UUID)
- user (User)
- preferredCategories (Set<Category>)
- interests (String)
- preferredLanguage (String)

### DeviceToken
- id (UUID)
- user (User)
- token (String)
- deviceType (String)
- createdAt (LocalDateTime)

### UserActivity
- id (UUID)
- user (User)
- action (String)
- endpoint (String)
- timestamp (LocalDateTime)
- ipAddress (String)

## Безопасность

### JWT аутентификация
- Токены JWT для аутентификации пользователей
- Срок действия токена: 24 часа
- Проверка ролей для защищенных эндпоинтов

### Rate limiting
- Ограничение количества запросов с одного IP
- Настраиваемые лимиты для разных эндпоинтов
- Использование Redis для хранения счетчиков

### Защита от атак
- CORS настройки
- Secure headers (Content-Security-Policy, X-XSS-Protection и др.)
- Input validation и sanitization

## Кэширование

### Redis Cache
- Кэширование популярных запросов на 15 минут
- Кэширование сессий пользователей
- Хранение токенов устройств

### Стратегии кэширования
- Кэширование фактов и статистики
- Кэширование категорий
- Кэширование результатов запросов к внешним API

## Мониторинг и метрики

### Spring Boot Actuator
- Health checks для проверки состояния приложения
- Метрики производительности
- Информация о состоянии приложения

### Prometheus и Grafana
- Сбор метрик с помощью Prometheus
- Визуализация метрик в Grafana
- Настраиваемые дашборды для мониторинга

### Логирование
- Структурированное логирование с использованием SLF4J
- Ротация логов
- Логирование ошибок и важных событий

## Интеграции

### OpenAI API
- Генерация фактов на основе категории и интересов пользователя
- Анализ релевантности новостей
- Создание персонализированного контента

### News API
- Получение новостей из внешних источников
- Фильтрация и категоризация новостей
- Автоматический сбор новостей каждые 30 минут

### Firebase Cloud Messaging
- Отправка push-уведомлений пользователям
- Ежедневная отправка "факта дня"
- Еженедельная сводка интересных новостей

## Развертывание

### Docker
- Dockerfile для сборки образа приложения
- Multi-stage build для оптимизации размера образа
- Настройка JVM для контейнера

### Docker Compose
- Запуск полного стека (app + postgres + redis + prometheus + grafana)
- Настройка зависимостей между сервисами
- Настройка volumes для хранения данных

### Переменные окружения
- Конфигурация через переменные окружения
- Настройка для разных сред (dev, test, prod)
- Секреты и ключи API

## Настройка

### Конфигурация приложения
- application.yml для основной конфигурации
- application-prod.yml для продакшн-конфигурации
- Настройка через переменные окружения

### Настройка базы данных
- Подключение к PostgreSQL
- Миграции с помощью Flyway
- Connection pooling с HikariCP

### Настройка Redis
- Подключение к Redis
- Настройка кэширования
- Настройка сессий

### Настройка внешних API
- Ключи API для внешних сервисов
- Таймауты и повторные попытки
- Обработка ошибок

### Автоматический сбор данных

#### Сбор статистики
- Автоматический сбор статистических данных из внешних API
- Поддержка различных категорий статистики (население, экономика, здравоохранение, образование, экология)
- Сбор данных по расписанию
- API для управления сбором статистики
- Настраиваемые источники данных

#### Сбор фактов
- Автоматический сбор интересных фактов из внешних источников
- Поддержка различных категорий фактов (общие знания, математика, история, наука)
- Сбор данных по расписанию
- API для управления сбором фактов
- Настраиваемые источники данных 