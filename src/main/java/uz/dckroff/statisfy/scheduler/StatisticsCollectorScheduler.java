package uz.dckroff.statisfy.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.dckroff.statisfy.service.StatisticsCollectorService;

/**
 * Планировщик для автоматического сбора статистики из внешних источников
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "statistics-collector.enabled", havingValue = "true", matchIfMissing = true)
public class StatisticsCollectorScheduler {

    private final StatisticsCollectorService statisticsCollectorService;

    /**
     * Запуск сбора всех видов статистики каждый день в 01:00
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void collectAllStatistics() {
        log.info("Запуск запланированного сбора статистики");
        try {
            int count = statisticsCollectorService.collectAllStatistics();
            log.info("Запланированный сбор статистики завершен. Собрано {} записей", count);
        } catch (Exception e) {
            log.error("Ошибка при запланированном сборе статистики", e);
        }
    }
    
    /**
     * Запуск сбора экономической статистики каждые 12 часов (курсы валют часто меняются)
     */
    @Scheduled(cron = "0 0 */12 * * ?")
    public void collectEconomicStatistics() {
        log.info("Запуск запланированного сбора экономической статистики");
        try {
            int count = statisticsCollectorService.collectEconomicStatistics();
            log.info("Запланированный сбор экономической статистики завершен. Собрано {} записей", count);
        } catch (Exception e) {
            log.error("Ошибка при запланированном сборе экономической статистики", e);
        }
    }
} 