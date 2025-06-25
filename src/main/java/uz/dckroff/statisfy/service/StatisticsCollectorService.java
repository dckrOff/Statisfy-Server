package uz.dckroff.statisfy.service;

/**
 * Сервис для автоматического сбора статистических данных из различных внешних источников
 */
public interface StatisticsCollectorService {
    
    /**
     * Собирает данные о населении по странам
     * @return количество собранных статистических записей
     */
    int collectPopulationStatistics();
    
    /**
     * Собирает данные об экономике (ВВП, инфляция и т.д.)
     * @return количество собранных статистических записей
     */
    int collectEconomicStatistics();
    
    /**
     * Собирает данные о здравоохранении
     * @return количество собранных статистических записей
     */
    int collectHealthStatistics();
    
    /**
     * Собирает данные об образовании
     * @return количество собранных статистических записей
     */
    int collectEducationStatistics();
    
    /**
     * Собирает данные о климате и экологии
     * @return количество собранных статистических записей
     */
    int collectEnvironmentStatistics();
    
    /**
     * Запускает сбор данных по всем доступным источникам и категориям
     * @return общее количество собранных статистических записей
     */
    int collectAllStatistics();
} 