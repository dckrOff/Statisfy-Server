package uz.dckroff.statisfy.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Getter
public class CustomMetricsConfiguration {

    private final MeterRegistry meterRegistry;
    
    // Счетчики для отслеживания бизнес-метрик
    private Counter factViewCounter;
    private Counter statisticViewCounter;
    private Counter newsViewCounter;
    private Counter loginCounter;
    private Counter registrationCounter;
    private Counter aiGenerationCounter;
    
    // Таймеры для измерения длительности операций
    private Timer factRetrievalTimer;
    private Timer statisticRetrievalTimer;
    private Timer newsRetrievalTimer;
    private Timer aiGenerationTimer;
    
    // Измерение активных сессий пользователей
    private AtomicInteger activeSessions;
    
    @Autowired
    public CustomMetricsConfiguration(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    @PostConstruct
    public void registerMetrics() {
        // Инициализация счетчиков
        factViewCounter = Counter.builder("app.facts.views")
                .description("Number of fact views")
                .register(meterRegistry);
        
        statisticViewCounter = Counter.builder("app.statistics.views")
                .description("Number of statistic views")
                .register(meterRegistry);
        
        newsViewCounter = Counter.builder("app.news.views")
                .description("Number of news views")
                .register(meterRegistry);
        
        loginCounter = Counter.builder("app.auth.logins")
                .description("Number of successful logins")
                .register(meterRegistry);
        
        registrationCounter = Counter.builder("app.auth.registrations")
                .description("Number of user registrations")
                .register(meterRegistry);
        
        aiGenerationCounter = Counter.builder("app.ai.generations")
                .description("Number of AI content generations")
                .register(meterRegistry);
        
        // Инициализация таймеров
        factRetrievalTimer = Timer.builder("app.facts.retrieval.time")
                .description("Time taken to retrieve facts")
                .register(meterRegistry);
        
        statisticRetrievalTimer = Timer.builder("app.statistics.retrieval.time")
                .description("Time taken to retrieve statistics")
                .register(meterRegistry);
        
        newsRetrievalTimer = Timer.builder("app.news.retrieval.time")
                .description("Time taken to retrieve news")
                .register(meterRegistry);
        
        aiGenerationTimer = Timer.builder("app.ai.generation.time")
                .description("Time taken to generate AI content")
                .register(meterRegistry);
        
        // Измерение активных сессий
        activeSessions = new AtomicInteger(0);
        meterRegistry.gauge("app.users.active_sessions", activeSessions);
    }
    
    /**
     * Увеличивает счетчик активных сессий
     */
    public void incrementActiveSessions() {
        activeSessions.incrementAndGet();
    }
    
    /**
     * Уменьшает счетчик активных сессий
     */
    public void decrementActiveSessions() {
        activeSessions.decrementAndGet();
    }
    
    /**
     * Записывает длительность операции получения фактов
     */
    public void recordFactRetrievalTime(long timeInMillis) {
        factRetrievalTimer.record(timeInMillis, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Записывает длительность операции получения статистики
     */
    public void recordStatisticRetrievalTime(long timeInMillis) {
        statisticRetrievalTimer.record(timeInMillis, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Записывает длительность операции получения новостей
     */
    public void recordNewsRetrievalTime(long timeInMillis) {
        newsRetrievalTimer.record(timeInMillis, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Записывает длительность операции генерации AI контента
     */
    public void recordAiGenerationTime(long timeInMillis) {
        aiGenerationTimer.record(timeInMillis, TimeUnit.MILLISECONDS);
    }
} 