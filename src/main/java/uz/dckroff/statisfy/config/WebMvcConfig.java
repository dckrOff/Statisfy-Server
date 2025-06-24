package uz.dckroff.statisfy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uz.dckroff.statisfy.interceptor.ActivityLoggingInterceptor;
import uz.dckroff.statisfy.interceptor.MetricsInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ActivityLoggingInterceptor activityLoggingInterceptor;
    private final MetricsInterceptor metricsInterceptor;
    private final ObjectMapper objectMapper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add metrics interceptor to measure complete request time
        registry.addInterceptor(metricsInterceptor);
        
        // Then add activity logging interceptor
        registry.addInterceptor(activityLoggingInterceptor);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Находим и заменяем стандартный MappingJackson2HttpMessageConverter
        for (int i = 0; i < converters.size(); i++) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                converters.set(i, new MappingJackson2HttpMessageConverter(objectMapper));
                break;
            }
        }
    }
} 