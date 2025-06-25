package uz.dckroff.statisfy.dto.fact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO с результатами сбора фактов из внешних источников
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactCollectorResponse {
    
    /**
     * Количество собранных фактов
     */
    private int factsCollected;
    
    /**
     * Название источника данных
     */
    private String source;
    
    /**
     * Категория фактов
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