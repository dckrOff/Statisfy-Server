package uz.dckroff.statisfy.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.dckroff.statisfy.repository.FactRepository;
import uz.dckroff.statisfy.repository.NewsRepository;
import uz.dckroff.statisfy.repository.StatisticRepository;
import uz.dckroff.statisfy.repository.UserRepository;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class MetricsConfig {

    private final AtomicInteger apiRequestsInProgress = new AtomicInteger(0);

    @Getter
    private final AtomicInteger getApiRequestsInProgress = apiRequestsInProgress;

    private final UserRepository userRepository;
    private final FactRepository factRepository;
    private final StatisticRepository statisticRepository;
    private final NewsRepository newsRepository;

    public MetricsConfig(
            UserRepository userRepository,
            FactRepository factRepository,
            StatisticRepository statisticRepository,
            NewsRepository newsRepository
    ) {
        this.userRepository = userRepository;
        this.factRepository = factRepository;
        this.statisticRepository = statisticRepository;
        this.newsRepository = newsRepository;
    }

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

    @PostConstruct
    public void registerCustomMetrics() {
        // ❗ MeterRegistry получаем из контекста позже, не через поле!
        // это важно для предотвращения циклов

        MeterRegistry registry = io.micrometer.core.instrument.Metrics.globalRegistry;

        Gauge.builder("api.requests.active", apiRequestsInProgress::get)
                .description("Number of active API requests")
                .register(registry);

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
