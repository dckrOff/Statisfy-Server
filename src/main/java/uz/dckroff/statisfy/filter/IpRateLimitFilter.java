package uz.dckroff.statisfy.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpRateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;
    
    private Counter rateLimitCounter;
    
    // Конфигурируемые параметры (можно вынести в application.yml)
    private static final int DEFAULT_LIMIT = 60;  // Запросов в минуту
    private static final int AUTH_LIMIT = 10;     // Запросов в минуту для аутентификации
    private static final int ADMIN_LIMIT = 120;   // Запросов в минуту для администраторов
    private static final int CONTENT_LIMIT = 30;  // Запросов в минуту для операций с контентом
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        if (rateLimitCounter == null) {
            rateLimitCounter = Counter.builder("api.rate_limit.exceeded")
                    .description("Number of rate limit exceeded events")
                    .register(meterRegistry);
        }
        
        String clientIp = getClientIP(request);
        String requestURI = request.getRequestURI();
        
        // Определяем лимит на основе URL
        int limit = getLimitByUrl(requestURI);
        
        // Ключ в Redis для хранения счетчика запросов
        String redisKey = "rate-limit:" + clientIp + ":" + getRateLimitCategory(requestURI);
        
        // Проверяем, превышен ли лимит
        Long currentCount = redisTemplate.opsForValue().increment(redisKey, 1);
        
        // Если ключ только что создан, устанавливаем время жизни (1 минута)
        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(redisKey, 60, TimeUnit.SECONDS);
        }
        
        if (currentCount != null && currentCount > limit) {
            // Лимит превышен
            rateLimitCounter.increment();
            log.warn("Rate limit exceeded for IP: {}, URI: {}, count: {}, limit: {}", 
                    clientIp, requestURI, currentCount, limit);
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            // Получаем время до истечения ключа
            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            Long retryAfter = ttl != null && ttl > 0 ? ttl : 60;
            
            response.addHeader("X-Rate-Limit-Limit", String.valueOf(limit));
            response.addHeader("X-Rate-Limit-Remaining", "0");
            response.addHeader("X-Rate-Limit-Reset", String.valueOf(retryAfter));
            response.addHeader("Retry-After", String.valueOf(retryAfter));
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("timestamp", LocalDateTime.now().toString());
            responseBody.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
            responseBody.put("error", "Too Many Requests");
            responseBody.put("message", "Вы превысили лимит запросов. Пожалуйста, повторите запрос позже.");
            responseBody.put("path", requestURI);
            responseBody.put("retryAfterSeconds", retryAfter);
            
            objectMapper.writeValue(response.getOutputStream(), responseBody);
            return;
        }
        
        // Добавляем заголовки о лимитах
        if (currentCount != null) {
            response.addHeader("X-Rate-Limit-Limit", String.valueOf(limit));
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(Math.max(0, limit - currentCount)));
            
            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            if (ttl != null && ttl > 0) {
                response.addHeader("X-Rate-Limit-Reset", String.valueOf(ttl));
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Получает IP-адрес клиента с учетом возможных прокси
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            String[] ips = xForwardedFor.split(",");
            return ips[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    /**
     * Определяет лимит на основе URL запроса
     */
    private int getLimitByUrl(String uri) {
        if (uri.startsWith("/api/auth")) {
            return AUTH_LIMIT;
        } else if (uri.startsWith("/api/admin")) {
            return ADMIN_LIMIT;
        } else if (uri.startsWith("/api/facts") || 
                  uri.startsWith("/api/statistics") || 
                  uri.startsWith("/api/news") ||
                  uri.startsWith("/api/ai")) {
            return CONTENT_LIMIT;
        } else {
            return DEFAULT_LIMIT;
        }
    }
    
    /**
     * Получает категорию для использования в ключе Redis
     */
    private String getRateLimitCategory(String uri) {
        if (uri.startsWith("/api/auth")) {
            return "auth";
        } else if (uri.startsWith("/api/admin")) {
            return "admin";
        } else if (uri.startsWith("/api/facts")) {
            return "facts";
        } else if (uri.startsWith("/api/statistics")) {
            return "statistics";
        } else if (uri.startsWith("/api/news")) {
            return "news";
        } else if (uri.startsWith("/api/ai")) {
            return "ai";
        } else {
            return "default";
        }
    }
} 