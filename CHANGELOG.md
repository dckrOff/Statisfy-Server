# Statisfy Server Changelog

## [0.9.0]
### Added
- Автоматический сбор статистики из внешних источников (World Bank API, Open Exchange Rates API)
- Автоматический сбор фактов из внешних источников (Wikipedia API, Numbers API, History API, Space News API)
- Планировщики для регулярного сбора данных по расписанию
- API для управления сбором статистики и фактов
- Конфигурация для источников данных в application.yml
- RestTemplate конфигурация для HTTP запросов к внешним API
- Обновлена документация с информацией о новых функциях

### Changed
- Улучшена структура документации проекта
- Обновлены README.md и DOCUMENTATION.md

## [0.8.0] 
### Added
- Redis интеграция для кэширования и управления сессиями
- Connection pooling с использованием HikariCP
- Асинхронная обработка тяжелых операций с TaskExecutor
- Rate Limiting с использованием Bucket4j и Redis
- Безопасные HTTP заголовки (Content-Security-Policy, X-Frame-Options и др.)
- Расширенные настройки CORS
- ETag поддержка и HTTP compression
- Логирование медленных запросов и производительности
- Оптимизация Hibernate с настройками кэширования и пакетной обработки

### Changed
- Обновлен docker-compose.yml для включения Redis
- Улучшены метрики и мониторинг производительности
- Реструктуризация настроек в application.yml

## [0.7.0] 
### Added
- Подробная документация API (api-documentation.md)
- Улучшенная обработка ошибок с информативными сообщениями
- Обработчики для AccessDeniedException и AuthenticationException
- Подробные сообщения об ошибках для JWT авторизации
- AccessDeniedHandler и AuthenticationEntryPoint в SecurityConfig
- Расширен белый список URL для публичного доступа
- Добавлен путь к ресурсу в сообщения об ошибках

### Changed
- Формат ответов об ошибках теперь содержит timestamp, status, error, message и path
- Улучшены сообщения об ошибках на русском языке
- Изменена структура GlobalExceptionHandler для большей информативности

## [0.6.0] 
### Added
- Analytics system for tracking user activity
- UserActivity model and repository
- AnalyticsService for collecting and querying analytics data
- API endpoints for analytics data
- Spring Boot Actuator integration for monitoring
- Custom metrics for application performance
- Interceptors for automatic activity logging
- Dashboard statistics for administrators
- Structured logging with log rotation
- Custom health indicators for external services

## [0.5.0] 
### Added
- Push notification system with Firebase Cloud Messaging
- Device token registration and management
- Notification settings for users
- Daily fact notifications
- Weekly news summary notifications
- Scheduled tasks for automated notifications
- API endpoints for notification management
- Database migrations for notification tables

## [0.4.1]
### Added
- Completed implementation of UserPreferenceService
- Added missing methods in UserPreferenceServiceImpl
- Created DTOs for AI requests and responses
- Added validation for AI request parameters
- Implemented proper error handling in AI services

### Fixed
- Updated deprecated RestTemplate methods to use modern API
- Fixed build issues and compilation errors
- Corrected validation annotations in DTOs
- Ensured compatibility with Spring Boot 3.x

## [0.4.0]
### Added
- Basic AI integration with OpenAI API
- AIService for generating facts and analyzing news relevance
- Daily personalized fact generation based on user preferences
- UserPreference model for storing user preferences
- UserPreferenceService for managing user preferences
- AIController with endpoints for AI-related operations
- UserPreferenceController for managing user preferences
- Database migration for user preferences tables
- OpenAI configuration in application.yml

## [0.3.0]
### Added
- News system implementation
- News model, repository, service and controller
- NewsAPI integration for fetching news
- Web scraping functionality using Jsoup
- Scrapers for kun.uz and gazeta.uz
- Scheduled news collection
- Caching for popular requests
- Endpoints for retrieving news

## [0.2.0]
### Added
- Content system for facts and statistics
- Category, Fact, and Statistic models
- Repositories, services, and controllers for content
- Role-based access control (USER, ADMIN)
- Validation and error handling
- Database migrations for content tables
- Pagination and filtering for content endpoints

## [0.1.0]
### Added
- Initial Spring Boot project setup
- PostgreSQL database configuration
- User model and authentication system
- JWT token implementation
- User registration and login endpoints
- Spring Security configuration
- Docker Compose for PostgreSQL
- Basic project structure 