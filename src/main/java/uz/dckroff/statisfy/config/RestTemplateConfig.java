package uz.dckroff.statisfy.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Конфигурация RestTemplate для выполнения HTTP запросов к внешним API
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Создает настроенный экземпляр RestTemplate с таймаутами и обработкой ошибок
     * @return настроенный RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }


    /**
     * Создает фабрику HTTP запросов с настроенными параметрами
     * @return настроенная фабрика HTTP запросов
     */
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return factory;
    }
} 