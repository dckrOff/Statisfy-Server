# Statisfy Server Changelog

## [0.4.1] - 2023-12-05
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

## [0.4.0] - 2023-11-25
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

## [0.3.0] - 2023-11-10
### Added
- News system implementation
- News model, repository, service and controller
- NewsAPI integration for fetching news
- Web scraping functionality using Jsoup
- Scrapers for kun.uz and gazeta.uz
- Scheduled news collection
- Caching for popular requests
- Endpoints for retrieving news

## [0.2.0] - 2023-10-25
### Added
- Content system for facts and statistics
- Category, Fact, and Statistic models
- Repositories, services, and controllers for content
- Role-based access control (USER, ADMIN)
- Validation and error handling
- Database migrations for content tables
- Pagination and filtering for content endpoints

## [0.1.0] - 2023-10-10
### Added
- Initial Spring Boot project setup
- PostgreSQL database configuration
- User model and authentication system
- JWT token implementation
- User registration and login endpoints
- Spring Security configuration
- Docker Compose for PostgreSQL
- Basic project structure 