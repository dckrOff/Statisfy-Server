package uz.dckroff.statisfy.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.dckroff.statisfy.repository.FactRepository;
import uz.dckroff.statisfy.repository.NewsRepository;
import uz.dckroff.statisfy.repository.StatisticRepository;
import uz.dckroff.statisfy.repository.UserRepository;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@RequiredArgsConstructor
public class MetricsConfig {

    private final UserRepository userRepository;
    private final FactRepository factRepository;
    private final StatisticRepository statisticRepository;
    private final NewsRepository newsRepository;
    
    private final AtomicInteger apiRequestsInProgress = new AtomicInteger(0);
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "statisfy-server");
    }
    
    @Bean
    public Counter apiRequestCounter(MeterRegistry registry) {
        return Counter.builder("api.requests.total")
                .description("Total number of API requests")
                .register(registry);
    }
    
    @Bean
    public Timer apiRequestTimer(MeterRegistry registry) {
        return Timer.builder("api.requests.duration")
                .description("API request duration")
                .register(registry);
    }
    
    @Bean
    public void registerCustomMetrics(MeterRegistry registry) {
        // Active API requests gauge
        Gauge.builder("api.requests.active", apiRequestsInProgress::get)
                .description("Number of active API requests")
                .register(registry);
        
        // Database entity counts
        Gauge.builder("db.users.count", userRepository::count)
                .description("Total number of users")
                .register(registry);
        
        Gauge.builder("db.facts.count", factRepository::count)
                .description("Total number of facts")
                .register(registry);
        
        Gauge.builder("db.statistics.count", statisticRepository::count)
                .description("Total number of statistics")
                .register(registry);
        
        Gauge.builder("db.news.count", newsRepository::count)
                .description("Total number of news")
                .register(registry);
    }
    
    public void incrementApiRequests() {
        apiRequestsInProgress.incrementAndGet();
    }
    
    public void decrementApiRequests() {
        apiRequestsInProgress.decrementAndGet();
    }
} 