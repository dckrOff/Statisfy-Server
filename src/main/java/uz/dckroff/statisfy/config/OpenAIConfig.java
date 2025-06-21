package uz.dckroff.statisfy.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.timeout:30}")
    private int timeout;

    @Bean
    public OpenAiService openAiService() {
        log.info("Initializing OpenAI service with timeout: {} seconds", timeout);
        return new OpenAiService(apiKey, Duration.ofSeconds(timeout));
    }
} 