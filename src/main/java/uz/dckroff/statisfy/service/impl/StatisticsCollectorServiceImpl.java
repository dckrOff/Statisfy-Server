package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import uz.dckroff.statisfy.config.StatisticsCollectorConfig;
import uz.dckroff.statisfy.dto.statistic.StatisticRequest;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.service.StatisticService;
import uz.dckroff.statisfy.service.StatisticsCollectorService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Реализация сервиса сбора статистики из внешних источников
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsCollectorServiceImpl implements StatisticsCollectorService {

    private final RestTemplate restTemplate;
    private final CategoryRepository categoryRepository;
    private final StatisticService statisticService;
    private final StatisticsCollectorConfig config;

    // API для получения данных о населении (World Bank API)
    private static final String POPULATION_API_URL = "http://api.worldbank.org/v2/country/all/indicator/SP.POP.TOTL?format=json&date=2022";
    
    // API для получения экономических данных (Open Exchange Rates API)
    private static final String ECONOMIC_API_URL = "https://openexchangerates.org/api/latest.json";
    
    // API для получения данных о здравоохранении (World Bank API)
    private static final String HEALTH_API_URL = "http://api.worldbank.org/v2/country/all/indicator/SH.MED.BEDS.ZS?format=json&date=2022";
    
    // API для получения данных об образовании (World Bank API)
    private static final String EDUCATION_API_URL = "http://api.worldbank.org/v2/country/all/indicator/SE.XPD.TOTL.GD.ZS?format=json&date=2022";
    
    // API для получения данных об экологии (World Bank API)
    private static final String ENVIRONMENT_API_URL = "http://api.worldbank.org/v2/country/all/indicator/EN.ATM.CO2E.PC?format=json&date=2022";

    @Override
    public int collectPopulationStatistics() {
        log.info("Начало сбора статистики о населении");
        if (!config.isEnabled()) {
            log.warn("Сбор статистики отключен в настройках");
            return 0;
        }
        
        AtomicInteger count = new AtomicInteger(0);
        
        try {
            // Получаем или создаем категорию для данных о населении
            Category category = getOrCreateCategory("Население", "Статистика о населении стран мира");
            
            // Настраиваем заголовки запроса
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Выполняем запрос к World Bank API
            ResponseEntity<Map> response = restTemplate.exchange(
                    POPULATION_API_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Получаем данные из ответа
                Map<String, Object> data = response.getBody();
                if (data.containsKey("1") && data.get("1") instanceof java.util.List) {
                    java.util.List<Map<String, Object>> countries = (java.util.List<Map<String, Object>>) data.get("1");
                    
                    // Обрабатываем данные каждой страны
                    for (Map<String, Object> countryData : countries) {
                        try {
                            // Проверяем наличие необходимых данных
                            if (countryData.containsKey("country") && countryData.containsKey("value") && countryData.get("value") != null) {
                                Map<String, Object> country = (Map<String, Object>) countryData.get("country");
                                String countryName = (String) country.get("value");
                                double population = Double.parseDouble(countryData.get("value").toString());
                                
                                // Создаем объект запроса для сохранения статистики
                                StatisticRequest request = StatisticRequest.builder()
                                        .title("Население " + countryName)
                                        .value(population)
                                        .unit("человек")
                                        .categoryId(category.getId())
                                        .source("World Bank")
                                        .date(LocalDate.now())
                                        .build();
                                
                                // Сохраняем статистику
                                statisticService.createStatistic(request);
                                count.incrementAndGet();
                                log.info("Сохранена статистика о населении для {}: {} человек", countryName, population);
                            }
                        } catch (Exception e) {
                            log.error("Ошибка при обработке данных о населении для страны", e);
                        }
                    }
                }
            }
            
            log.info("Завершен сбор статистики о населении. Добавлено {} записей", count.get());
            return count.get();
        } catch (RestClientException e) {
            log.error("Ошибка при получении данных о населении из API", e);
            return 0;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сборе статистики о населении", e);
            return 0;
        }
    }

    @Override
    public int collectEconomicStatistics() {
        log.info("Начало сбора экономической статистики");
        if (!config.isEnabled()) {
            log.warn("Сбор статистики отключен в настройках");
            return 0;
        }
        
        AtomicInteger count = new AtomicInteger(0);
        
        try {
            // Получаем или создаем категорию для экономических данных
            Category category = getOrCreateCategory("Экономика", "Экономическая статистика");
            
            // Настраиваем заголовки запроса
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Формируем URL с API ключом
            String apiUrl = ECONOMIC_API_URL;
            // Если в настройках есть источник с именем "openexchangerates", используем его API ключ
            for (StatisticsCollectorConfig.StatisticsSource source : config.getSources()) {
                if ("openexchangerates".equals(source.getName()) && source.getApiKey() != null) {
                    apiUrl = apiUrl + "?app_id=" + source.getApiKey();
                    break;
                }
            }
            
            // Выполняем запрос к Open Exchange Rates API
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Получаем данные из ответа
                Map<String, Object> data = response.getBody();
                if (data.containsKey("rates")) {
                    Map<String, Object> rates = (Map<String, Object>) data.get("rates");
                    String baseRate = (String) data.getOrDefault("base", "USD");
                    
                    // Сохраняем курсы валют
                    for (Map.Entry<String, Object> entry : rates.entrySet()) {
                        try {
                            String currency = entry.getKey();
                            double rate = Double.parseDouble(entry.getValue().toString());
                            
                            // Создаем объект запроса для сохранения статистики
                            StatisticRequest request = StatisticRequest.builder()
                                    .title("Курс " + currency + " к " + baseRate)
                                    .value(rate)
                                    .unit(currency)
                                    .categoryId(category.getId())
                                    .source("Open Exchange Rates")
                                    .date(LocalDate.now())
                                    .build();
                            
                            // Сохраняем статистику
                            statisticService.createStatistic(request);
                            count.incrementAndGet();
                            log.info("Сохранен курс валюты: {} = {} {}", currency, rate, baseRate);
                        } catch (Exception e) {
                            log.error("Ошибка при обработке курса валюты {}", entry.getKey(), e);
                        }
                    }
                }
            }
            
            log.info("Завершен сбор экономической статистики. Добавлено {} записей", count.get());
            return count.get();
        } catch (RestClientException e) {
            log.error("Ошибка при получении экономических данных из API", e);
            return 0;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сборе экономической статистики", e);
            return 0;
        }
    }

    @Override
    public int collectHealthStatistics() {
        log.info("Начало сбора статистики о здравоохранении");
        if (!config.isEnabled()) {
            log.warn("Сбор статистики отключен в настройках");
            return 0;
        }
        
        AtomicInteger count = new AtomicInteger(0);
        
        try {
            // Получаем или создаем категорию для данных о здравоохранении
            Category category = getOrCreateCategory("Здравоохранение", "Статистика о здравоохранении");
            
            // Настраиваем заголовки запроса
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Выполняем запрос к World Bank API
            ResponseEntity<Map> response = restTemplate.exchange(
                    HEALTH_API_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Получаем данные из ответа
                Map<String, Object> data = response.getBody();
                if (data.containsKey("1") && data.get("1") instanceof java.util.List) {
                    java.util.List<Map<String, Object>> countries = (java.util.List<Map<String, Object>>) data.get("1");
                    
                    // Обрабатываем данные каждой страны
                    for (Map<String, Object> countryData : countries) {
                        try {
                            // Проверяем наличие необходимых данных
                            if (countryData.containsKey("country") && countryData.containsKey("value") && countryData.get("value") != null) {
                                Map<String, Object> country = (Map<String, Object>) countryData.get("country");
                                String countryName = (String) country.get("value");
                                double bedsValue = Double.parseDouble(countryData.get("value").toString());
                                
                                // Создаем объект запроса для сохранения статистики
                                StatisticRequest request = StatisticRequest.builder()
                                        .title("Больничные койки в " + countryName)
                                        .value(bedsValue)
                                        .unit("на 1000 человек")
                                        .categoryId(category.getId())
                                        .source("World Bank")
                                        .date(LocalDate.now())
                                        .build();
                                
                                // Сохраняем статистику
                                statisticService.createStatistic(request);
                                count.incrementAndGet();
                                log.info("Сохранена статистика о здравоохранении для {}: {} коек на 1000 человек", countryName, bedsValue);
                            }
                        } catch (Exception e) {
                            log.error("Ошибка при обработке данных о здравоохранении для страны", e);
                        }
                    }
                }
            }
            
            log.info("Завершен сбор статистики о здравоохранении. Добавлено {} записей", count.get());
            return count.get();
        } catch (RestClientException e) {
            log.error("Ошибка при получении данных о здравоохранении из API", e);
            return 0;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сборе статистики о здравоохранении", e);
            return 0;
        }
    }

    @Override
    public int collectEducationStatistics() {
        log.info("Начало сбора статистики об образовании");
        if (!config.isEnabled()) {
            log.warn("Сбор статистики отключен в настройках");
            return 0;
        }
        
        AtomicInteger count = new AtomicInteger(0);
        
        try {
            // Получаем или создаем категорию для данных об образовании
            Category category = getOrCreateCategory("Образование", "Статистика об образовании");
            
            // Настраиваем заголовки запроса
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Выполняем запрос к World Bank API
            ResponseEntity<Map> response = restTemplate.exchange(
                    EDUCATION_API_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Получаем данные из ответа
                Map<String, Object> data = response.getBody();
                if (data.containsKey("1") && data.get("1") instanceof java.util.List) {
                    java.util.List<Map<String, Object>> countries = (java.util.List<Map<String, Object>>) data.get("1");
                    
                    // Обрабатываем данные каждой страны
                    for (Map<String, Object> countryData : countries) {
                        try {
                            // Проверяем наличие необходимых данных
                            if (countryData.containsKey("country") && countryData.containsKey("value") && countryData.get("value") != null) {
                                Map<String, Object> country = (Map<String, Object>) countryData.get("country");
                                String countryName = (String) country.get("value");
                                double educationSpending = Double.parseDouble(countryData.get("value").toString());
                                
                                // Создаем объект запроса для сохранения статистики
                                StatisticRequest request = StatisticRequest.builder()
                                        .title("Расходы на образование в " + countryName)
                                        .value(educationSpending)
                                        .unit("% от ВВП")
                                        .categoryId(category.getId())
                                        .source("World Bank")
                                        .date(LocalDate.now())
                                        .build();
                                
                                // Сохраняем статистику
                                statisticService.createStatistic(request);
                                count.incrementAndGet();
                                log.info("Сохранена статистика об образовании для {}: {}% от ВВП", countryName, educationSpending);
                            }
                        } catch (Exception e) {
                            log.error("Ошибка при обработке данных об образовании для страны", e);
                        }
                    }
                }
            }
            
            log.info("Завершен сбор статистики об образовании. Добавлено {} записей", count.get());
            return count.get();
        } catch (RestClientException e) {
            log.error("Ошибка при получении данных об образовании из API", e);
            return 0;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сборе статистики об образовании", e);
            return 0;
        }
    }

    @Override
    public int collectEnvironmentStatistics() {
        log.info("Начало сбора статистики об экологии");
        if (!config.isEnabled()) {
            log.warn("Сбор статистики отключен в настройках");
            return 0;
        }
        
        AtomicInteger count = new AtomicInteger(0);
        
        try {
            // Получаем или создаем категорию для данных об экологии
            Category category = getOrCreateCategory("Экология", "Статистика об экологии и климате");
            
            // Настраиваем заголовки запроса
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Выполняем запрос к World Bank API
            ResponseEntity<Map> response = restTemplate.exchange(
                    ENVIRONMENT_API_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Получаем данные из ответа
                Map<String, Object> data = response.getBody();
                if (data.containsKey("1") && data.get("1") instanceof java.util.List) {
                    java.util.List<Map<String, Object>> countries = (java.util.List<Map<String, Object>>) data.get("1");
                    
                    // Обрабатываем данные каждой страны
                    for (Map<String, Object> countryData : countries) {
                        try {
                            // Проверяем наличие необходимых данных
                            if (countryData.containsKey("country") && countryData.containsKey("value") && countryData.get("value") != null) {
                                Map<String, Object> country = (Map<String, Object>) countryData.get("country");
                                String countryName = (String) country.get("value");
                                double co2Emissions = Double.parseDouble(countryData.get("value").toString());
                                
                                // Создаем объект запроса для сохранения статистики
                                StatisticRequest request = StatisticRequest.builder()
                                        .title("Выбросы CO2 в " + countryName)
                                        .value(co2Emissions)
                                        .unit("тонн на душу населения")
                                        .categoryId(category.getId())
                                        .source("World Bank")
                                        .date(LocalDate.now())
                                        .build();
                                
                                // Сохраняем статистику
                                statisticService.createStatistic(request);
                                count.incrementAndGet();
                                log.info("Сохранена статистика об экологии для {}: {} тонн CO2 на душу населения", countryName, co2Emissions);
                            }
                        } catch (Exception e) {
                            log.error("Ошибка при обработке данных об экологии для страны", e);
                        }
                    }
                }
            }
            
            log.info("Завершен сбор статистики об экологии. Добавлено {} записей", count.get());
            return count.get();
        } catch (RestClientException e) {
            log.error("Ошибка при получении данных об экологии из API", e);
            return 0;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сборе статистики об экологии", e);
            return 0;
        }
    }

    @Override
    public int collectAllStatistics() {
        log.info("Запуск сбора всех категорий статистики");
        
        if (!config.isEnabled()) {
            log.warn("Сбор статистики отключен в настройках");
            return 0;
        }
        
        int populationCount = collectPopulationStatistics();
        int economicCount = collectEconomicStatistics();
        int healthCount = collectHealthStatistics();
        int educationCount = collectEducationStatistics();
        int environmentCount = collectEnvironmentStatistics();
        
        int totalCount = populationCount + economicCount + healthCount + educationCount + environmentCount;
        
        log.info("Завершен сбор всей статистики. Всего добавлено {} записей", totalCount);
        
        return totalCount;
    }
    
    /**
     * Создаёт заголовки HTTP для запросов к API
     * @return HttpHeaders с настроенными заголовками
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", config.getUserAgent());
        return headers;
    }
    
    /**
     * Получает существующую категорию или создаёт новую
     * @param name Название категории
     * @param description Описание категории
     * @return Объект категории
     */
    private Category getOrCreateCategory(String name, String description) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .name(name)
                            .description(description)
                            .build();
                    return categoryRepository.save(newCategory);
                });
    }
}