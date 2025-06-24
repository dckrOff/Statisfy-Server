package uz.dckroff.statisfy.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Дефолтная конфигурация кэша - срок жизни 1 час
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Специфичные конфигурации для разных типов кэшей
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        
        // Кэш для фактов и статистики - живет дольше (3 часа)
        cacheConfigs.put("facts", defaultConfig.entryTtl(Duration.ofHours(3)));
        cacheConfigs.put("statistics", defaultConfig.entryTtl(Duration.ofHours(3)));
        
        // Кэш для категорий - живет еще дольше (12 часов)
        cacheConfigs.put("categories", defaultConfig.entryTtl(Duration.ofHours(12)));
        
        // Кэш для новостей - короткое время жизни (30 минут)
        cacheConfigs.put("news", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // Кэш для аналитики - обновляется каждый час
        cacheConfigs.put("analytics", defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
} 