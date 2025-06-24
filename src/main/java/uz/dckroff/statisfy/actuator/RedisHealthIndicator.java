package uz.dckroff.statisfy.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component("redisHealthIndicator")
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public Health health() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            String pong = new String(connection.ping());
            if ("PONG".equals(pong)) {
                return Health.up()
                        .withDetail("redis", "UP")
                        .withDetail("version", connection.info().getProperty("redis_version"))
                        .build();
            } else {
                return Health.down()
                        .withDetail("redis", "DOWN")
                        .withDetail("response", pong)
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("redis", "DOWN")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
} 