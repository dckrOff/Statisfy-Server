package uz.dckroff.statisfy.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Конфигурация для RestTemplate, используемого для HTTP-запросов к внешним API
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Создает и настраивает экземпляр RestTemplate
     * @param builder Автоконфигурируемый билдер RestTemplate
     * @return Настроенный экземпляр RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }
}