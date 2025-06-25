package uz.dckroff.statisfy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.fact.FactCollectorResponse;
import uz.dckroff.statisfy.service.FactCollectorService;

/**
 * Контроллер для управления сбором фактов из внешних источников
 */
@RestController
@RequestMapping("/api/fact-collector")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class FactCollectorController {

    private final FactCollectorService factCollectorService;

    /**
     * Запускает сбор всех видов фактов
     * @return Результат операции
     */
    @PostMapping("/run-all")
    public ResponseEntity<FactCollectorResponse> collectAllFacts() {
        try {
            int count = factCollectorService.collectAllFacts();
            return ResponseEntity.ok(FactCollectorResponse.builder()
                    .factsCollected(count)
                    .source("Все источники")
                    .category("Все категории")
                    .success(true)
                    .message("Успешно собрано " + count + " фактов")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(FactCollectorResponse.builder()
                    .factsCollected(0)
                    .source("Все источники")
                    .category("Все категории")
                    .success(false)
                    .message("Ошибка при сборе фактов: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Запускает сбор фактов из Wikipedia
     * @return Результат операции
     */
    @PostMapping("/wikipedia")
    public ResponseEntity<FactCollectorResponse> collectWikipediaFacts() {
        try {
            int count = factCollectorService.collectWikipediaFacts();
            return ResponseEntity.ok(FactCollectorResponse.builder()
                    .factsCollected(count)
                    .source("Wikipedia")
                    .category("Общие знания")
                    .success(true)
                    .message("Успешно собрано " + count + " фактов из Wikipedia")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(FactCollectorResponse.builder()
                    .factsCollected(0)
                    .source("Wikipedia")
                    .category("Общие знания")
                    .success(false)
                    .message("Ошибка при сборе фактов из Wikipedia: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Запускает сбор фактов о числах
     * @return Результат операции
     */
    @PostMapping("/numbers")
    public ResponseEntity<FactCollectorResponse> collectNumbersFacts() {
        try {
            int count = factCollectorService.collectNumbersFacts();
            return ResponseEntity.ok(FactCollectorResponse.builder()
                    .factsCollected(count)
                    .source("Numbers API")
                    .category("Математика")
                    .success(true)
                    .message("Успешно собрано " + count + " фактов о числах")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(FactCollectorResponse.builder()
                    .factsCollected(0)
                    .source("Numbers API")
                    .category("Математика")
                    .success(false)
                    .message("Ошибка при сборе фактов о числах: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Запускает сбор исторических фактов
     * @return Результат операции
     */
    @PostMapping("/historical")
    public ResponseEntity<FactCollectorResponse> collectHistoricalFacts() {
        try {
            int count = factCollectorService.collectHistoricalFacts();
            return ResponseEntity.ok(FactCollectorResponse.builder()
                    .factsCollected(count)
                    .source("History API")
                    .category("История")
                    .success(true)
                    .message("Успешно собрано " + count + " исторических фактов")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(FactCollectorResponse.builder()
                    .factsCollected(0)
                    .source("History API")
                    .category("История")
                    .success(false)
                    .message("Ошибка при сборе исторических фактов: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Запускает сбор научных фактов
     * @return Результат операции
     */
    @PostMapping("/science")
    public ResponseEntity<FactCollectorResponse> collectScienceFacts() {
        try {
            int count = factCollectorService.collectScienceFacts();
            return ResponseEntity.ok(FactCollectorResponse.builder()
                    .factsCollected(count)
                    .source("Space News API")
                    .category("Наука")
                    .success(true)
                    .message("Успешно собрано " + count + " научных фактов")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(FactCollectorResponse.builder()
                    .factsCollected(0)
                    .source("Space News API")
                    .category("Наука")
                    .success(false)
                    .message("Ошибка при сборе научных фактов: " + e.getMessage())
                    .build());
        }
    }
} 