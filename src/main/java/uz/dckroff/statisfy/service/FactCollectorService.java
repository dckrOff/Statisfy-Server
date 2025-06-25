package uz.dckroff.statisfy.service;

/**
 * Сервис для автоматического сбора интересных фактов из различных внешних источников
 */
public interface FactCollectorService {
    
    /**
     * Собирает факты из Wikipedia API
     * @return количество собранных фактов
     */
    int collectWikipediaFacts();
    
    /**
     * Собирает факты из Numbers API
     * @return количество собранных фактов
     */
    int collectNumbersFacts();
    
    /**
     * Собирает исторические факты
     * @return количество собранных фактов
     */
    int collectHistoricalFacts();
    
    /**
     * Собирает научные факты
     * @return количество собранных фактов
     */
    int collectScienceFacts();
    
    /**
     * Запускает сбор фактов из всех доступных источников
     * @return общее количество собранных фактов
     */
    int collectAllFacts();
} 