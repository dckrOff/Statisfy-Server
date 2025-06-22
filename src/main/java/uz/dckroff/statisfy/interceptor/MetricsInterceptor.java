package uz.dckroff.statisfy.interceptor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import uz.dckroff.statisfy.config.MetricsConfig;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsInterceptor implements HandlerInterceptor {

    private final Counter apiRequestCounter;
    private final Timer apiRequestTimer;
    private final MetricsConfig metricsConfig;
    
    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            // Increment the counter for API requests
            apiRequestCounter.increment();
            
            // Increment active requests gauge
            metricsConfig.incrementApiRequests();
            
            // Store the start time for timing
            request.setAttribute(START_TIME_ATTRIBUTE, System.nanoTime());
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            // Decrement active requests gauge
            metricsConfig.decrementApiRequests();
            
            // Record the request duration
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
            if (startTime != null) {
                long duration = System.nanoTime() - startTime;
                apiRequestTimer.record(duration, TimeUnit.NANOSECONDS);
                
                // Log slow requests (more than 1 second)
                if (duration > 1_000_000_000) {
                    log.warn("Slow API request: {} {} - {}ms", 
                            request.getMethod(), 
                            request.getRequestURI(), 
                            duration / 1_000_000);
                }
            }
        }
    }
} 