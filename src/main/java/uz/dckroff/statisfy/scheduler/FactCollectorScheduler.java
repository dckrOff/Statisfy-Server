package uz.dckroff.statisfy.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.dckroff.statisfy.service.FactCollectorService;

/**
 * Планировщик для автоматического сбора фактов из внешних источников
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "fact-collector.enabled", havingValue = "true", matchIfMissing = true)
public class FactCollectorScheduler {

    private final FactCollectorService factCollectorService;

    /**
     * Запуск сбора всех видов фактов каждый день в 02:00
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void collectAllFacts() {
        log.info("Запуск запланированного сбора фактов");
        try {
            int count = factCollectorService.collectAllFacts();
            log.info("Запланированный сбор фактов завершен. Собрано {} фактов", count);
        } catch (Exception e) {
            log.error("Ошибка при запланированном сборе фактов", e);
        }
    }
    
    /**
     * Запуск сбора научных фактов каждые 2 дня в 04:00
     */
    @Scheduled(cron = "0 0 4 */2 * ?")
    public void collectScienceFacts() {
        log.info("Запуск запланированного сбора научных фактов");
        try {
            int count = factCollectorService.collectScienceFacts();
            log.info("Запланированный сбор научных фактов завершен. Собрано {} фактов", count);
        } catch (Exception e) {
            log.error("Ошибка при запланированном сборе научных фактов", e);
        }
    }
    
    /**
     * Запуск сбора исторических фактов каждую неделю в понедельник в 03:00
     */
    @Scheduled(cron = "0 0 3 ? * MON")
    public void collectHistoricalFacts() {
        log.info("Запуск запланированного сбора исторических фактов");
        try {
            int count = factCollectorService.collectHistoricalFacts();
            log.info("Запланированный сбор исторических фактов завершен. Собрано {} фактов", count);
        } catch (Exception e) {
            log.error("Ошибка при запланированном сборе исторических фактов", e);
        }
    }
} 