package uz.dckroff.statisfy.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uz.dckroff.statisfy.repository.NewsRepository;
import uz.dckroff.statisfy.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {

    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    @Override
    public Health health() {
        try {
            // Check database connectivity by executing simple queries
            long userCount = userRepository.count();
            long newsCount = newsRepository.count();
            
            // Check if external services are configured
            boolean newsApiConfigured = System.getenv("NEWS_API_KEY") != null;
            boolean openAiConfigured = System.getenv("OPENAI_API_KEY") != null;
            
            return Health.up()
                    .withDetail("database", "UP")
                    .withDetail("users", userCount)
                    .withDetail("news", newsCount)
                    .withDetail("newsApi", newsApiConfigured ? "CONFIGURED" : "NOT_CONFIGURED")
                    .withDetail("openAi", openAiConfigured ? "CONFIGURED" : "NOT_CONFIGURED")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "DOWN")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
} 