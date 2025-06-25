package uz.dckroff.statisfy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.statistic.StatisticsCollectorResponse;
import uz.dckroff.statisfy.service.StatisticsCollectorService;

/**
 * Контроллер для управления сбором статистики из внешних источников
 */
@RestController
@RequestMapping("/api/statistics-collector")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class StatisticsCollectorController {

    private final StatisticsCollectorService statisticsCollectorService;

    /**
     * Запускает сбор всех видов статистики
     * @return Результат операции
     */
    @PostMapping("/run-all")
    public ResponseEntity<StatisticsCollectorResponse> collectAllStatistics() {
        try {
            int count = statisticsCollectorService.collectAllStatistics();
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(count)
                    .source("Все источники")
                    .category("Все категории")
                    .success(true)
                    .message("Успешно собрано " + count + " статистических записей")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(0)
                    .source("Все источники")
                    .category("Все категории")
                    .success(false)
                    .message("Ошибка при сборе статистики: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Запускает сбор статистики о населении
     * @return Результат операции
     */
    @PostMapping("/population")
    public ResponseEntity<StatisticsCollectorResponse> collectPopulationStatistics() {
        try {
            int count = statisticsCollectorService.collectPopulationStatistics();
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(count)
                    .source("World Bank API")
                    .category("Население")
                    .success(true)
                    .message("Успешно собрано " + count + " записей о населении")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(0)
                    .source("World Bank API")
                    .category("Население")
                    .success(false)
                    .message("Ошибка при сборе статистики о населении: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Запускает сбор экономической статистики
     * @return Результат операции
     */
    @PostMapping("/economic")
    public ResponseEntity<StatisticsCollectorResponse> collectEconomicStatistics() {
        try {
            int count = statisticsCollectorService.collectEconomicStatistics();
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(count)
                    .source("Open Exchange Rates API")
                    .category("Экономика")
                    .success(true)
                    .message("Успешно собрано " + count + " записей по экономике")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(0)
                    .source("Open Exchange Rates API")
                    .category("Экономика")
                    .success(false)
                    .message("Ошибка при сборе экономической статистики: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Запускает сбор статистики о здравоохранении
     * @return Результат операции
     */
    @PostMapping("/health")
    public ResponseEntity<StatisticsCollectorResponse> collectHealthStatistics() {
        try {
            int count = statisticsCollectorService.collectHealthStatistics();
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(count)
                    .source("World Bank API")
                    .category("Здравоохранение")
                    .success(true)
                    .message("Успешно собрано " + count + " записей о здравоохранении")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(0)
                    .source("World Bank API")
                    .category("Здравоохранение")
                    .success(false)
                    .message("Ошибка при сборе статистики о здравоохранении: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Запускает сбор статистики об образовании
     * @return Результат операции
     */
    @PostMapping("/education")
    public ResponseEntity<StatisticsCollectorResponse> collectEducationStatistics() {
        try {
            int count = statisticsCollectorService.collectEducationStatistics();
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(count)
                    .source("World Bank API")
                    .category("Образование")
                    .success(true)
                    .message("Успешно собрано " + count + " записей об образовании")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(0)
                    .source("World Bank API")
                    .category("Образование")
                    .success(false)
                    .message("Ошибка при сборе статистики об образовании: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Запускает сбор экологической статистики
     * @return Результат операции
     */
    @PostMapping("/environment")
    public ResponseEntity<StatisticsCollectorResponse> collectEnvironmentStatistics() {
        try {
            int count = statisticsCollectorService.collectEnvironmentStatistics();
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(count)
                    .source("World Bank API")
                    .category("Экология")
                    .success(true)
                    .message("Успешно собрано " + count + " записей об экологии")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(StatisticsCollectorResponse.builder()
                    .statisticsCollected(0)
                    .source("World Bank API")
                    .category("Экология")
                    .success(false)
                    .message("Ошибка при сборе экологической статистики: " + e.getMessage())
                    .build());
        }
    }
} 