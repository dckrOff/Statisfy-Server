package uz.dckroff.statisfy.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO с результатами сбора статистики из внешних источников
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsCollectorResponse {
    
    /**
     * Количество собранных статистических записей
     */
    private int statisticsCollected;
    
    /**
     * Название источника данных
     */
    private String source;
    
    /**
     * Категория статистики
     */
    private String category;
    
    /**
     * Статус выполнения операции
     */
    private boolean success;
    
    /**
     * Сообщение о результате операции
     */
    private String message;
} 