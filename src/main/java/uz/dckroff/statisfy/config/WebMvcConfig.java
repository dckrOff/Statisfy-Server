package uz.dckroff.statisfy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uz.dckroff.statisfy.interceptor.ActivityLoggingInterceptor;
import uz.dckroff.statisfy.interceptor.MetricsInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ActivityLoggingInterceptor activityLoggingInterceptor;
    private final MetricsInterceptor metricsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add metrics interceptor first to measure complete request time
        registry.addInterceptor(metricsInterceptor);
        
        // Then add activity logging interceptor
        registry.addInterceptor(activityLoggingInterceptor);
    }
} 