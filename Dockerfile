FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
# Скачиваем зависимости для кэширования слоя
RUN gradle dependencies --no-daemon

# Копируем исходный код и собираем приложение
COPY src ./src
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Устанавливаем необходимые утилиты для healthcheck
RUN apk add --no-cache curl wget

# Создаем непривилегированного пользователя
RUN addgroup -S statisfy && adduser -S statisfy -G statisfy

# Директории для приложения
RUN mkdir -p /app/logs /app/uploads && \
    chown -R statisfy:statisfy /app

# Копируем файл JAR из этапа сборки
COPY --from=build --chown=statisfy:statisfy /app/build/libs/*.jar app.jar

# Переменные окружения по умолчанию
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV POSTGRES_HOST=postgres
ENV POSTGRES_PORT=5432
ENV POSTGRES_DB=statisfy
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=1234
ENV REDIS_HOST=redis
ENV REDIS_PORT=6379
ENV NEWS_API_KEY=your_news_api_key_here
ENV OPENAI_API_KEY=your_openai_api_key_here
ENV JVM_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError -XX:HeapDumpPath=/app/logs"

# Expose the port
EXPOSE 8080

# Проверка здоровья приложения
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health/liveness || exit 1

# Переключаемся на непривилегированного пользователя
USER statisfy

# Запуск приложения
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"] 