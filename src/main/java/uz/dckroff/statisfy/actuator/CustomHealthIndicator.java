package uz.dckroff.statisfy.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import uz.dckroff.statisfy.repository.FactRepository;
import uz.dckroff.statisfy.repository.NewsRepository;
import uz.dckroff.statisfy.repository.StatisticRepository;
import uz.dckroff.statisfy.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {

    private final UserRepository userRepository;
    private final NewsRepository newsRepository;
    private final FactRepository factRepository;
    private final StatisticRepository statisticRepository;
    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        Health.Builder builder = new Health.Builder();
        
        try {
            // Проверяем базу данных
            long userCount = userRepository.count();
            long newsCount = newsRepository.count();
            long factCount = factRepository.count();
            long statisticCount = statisticRepository.count();
            
            details.put("database", "UP");
            details.put("users", userCount);
            details.put("news", newsCount);
            details.put("facts", factCount);
            details.put("statistics", statisticCount);
            
            // Проверка соединения с Redis
            try {
                String result = redisTemplate.execute(connection -> {
                    return new String(connection.ping());
                }, true);
                
                if ("PONG".equals(result)) {
                    details.put("redis", "UP");
                } else {
                    details.put("redis", "DOWN");
                    details.put("redisError", "Unexpected response: " + result);
                    builder.status(Status.DOWN);
                }
            } catch (Exception e) {
                details.put("redis", "DOWN");
                details.put("redisError", e.getMessage());
                builder.status(Status.DOWN);
            }
            
            // Проверка API ключей
            boolean newsApiConfigured = System.getenv("NEWS_API_KEY") != null;
            boolean openAiConfigured = System.getenv("OPENAI_API_KEY") != null;
            
            details.put("newsApi", newsApiConfigured ? "CONFIGURED" : "NOT_CONFIGURED");
            details.put("openAi", openAiConfigured ? "CONFIGURED" : "NOT_CONFIGURED");
            
            // Проверка настроек JWT
            String jwtSecret = System.getenv("JWT_SECRET");
            details.put("jwt", jwtSecret != null && !jwtSecret.isEmpty() ? "CONFIGURED" : "USING_DEFAULT");
            
            // Проверка Firebase
            boolean firebaseEnabled = Boolean.parseBoolean(System.getenv("FIREBASE_ENABLED"));
            details.put("firebase", firebaseEnabled ? "ENABLED" : "DISABLED");
            
            // Проверка дискового пространства
            Runtime runtime = Runtime.getRuntime();
            details.put("memory.free", runtime.freeMemory());
            details.put("memory.total", runtime.totalMemory());
            details.put("memory.max", runtime.maxMemory());
            
            if (!builder.build().getStatus().equals(Status.DOWN)) {
                builder.up();
            }
            
        } catch (Exception e) {
            details.put("database", "DOWN");
            details.put("error", e.getMessage());
            builder.down(e);
        }
        
        return builder.withDetails(details).build();
    }
} 