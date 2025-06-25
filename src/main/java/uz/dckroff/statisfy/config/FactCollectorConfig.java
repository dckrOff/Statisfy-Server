package uz.dckroff.statisfy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Конфигурация для сервиса сбора фактов
 */
@Configuration
@ConfigurationProperties(prefix = "fact-collector")
@Data
public class FactCollectorConfig {
    
    /**
     * Включен ли сбор фактов
     */
    private boolean enabled = true;
    
    /**
     * Таймаут для HTTP-запросов в миллисекундах
     */
    private int connectionTimeout = 10000;
    
    /**
     * Пользовательский агент для HTTP-запросов
     */
    private String userAgent = "Statisfy-FactCollector/1.0";
    
    /**
     * Максимальное количество фактов для сбора за один запуск
     */
    private int maxFactsPerRun = 50;
    
    /**
     * Конфигурация для источников фактов
     */
    private List<FactSource> sources = new ArrayList<>();
    
    /**
     * Конфигурация для конкретного источника фактов
     */
    @Data
    public static class FactSource {
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
         * Категория фактов
         */
        private String category;
        
        /**
         * Включен ли этот источник
         */
        private boolean enabled = true;
    }
} 