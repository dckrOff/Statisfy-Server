package uz.dckroff.statisfy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Конфигурация для сервиса сбора статистики
 */
@Configuration
@ConfigurationProperties(prefix = "statistics-collector")
@Data
public class StatisticsCollectorConfig {
    
    /**
     * Включен ли сбор статистики
     */
    private boolean enabled = true;
    
    /**
     * Таймаут для HTTP-запросов в миллисекундах
     */
    private int connectionTimeout = 10000;
    
    /**
     * Пользовательский агент для HTTP-запросов
     */
    private String userAgent = "Statisfy-StatisticsCollector/1.0";
    
    /**
     * Конфигурация для источников статистики
     */
    private List<StatisticsSource> sources = new ArrayList<>();
    
    /**
     * Конфигурация для конкретного источника статистики
     */
    @Data
    public static class StatisticsSource {
        /**
         * Название источника
         */
        private String name;
        
        /**
         * URL API источника
         */
        private String url;
        
        /**
         * API ключ (если требуется)
         */
        private String apiKey;
        
        /**
         * Категория статистики
         */
        private String category;
        
        /**
         * Включен ли этот источник
         */
        private boolean enabled = true;
    }
} 