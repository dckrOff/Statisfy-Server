package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
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
import java.util.List;
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
    private static final String ECONOMIC_API_URL = "https://openexchangerates.org/api/latest.json?app_id=00611bd639904a97957714d3263e8928";

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

        int count = 0;

        try {
            Category category = getOrCreateCategory("Population", "Statistics about population by country");
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Получаем ответ от World Bank API
            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    POPULATION_API_URL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                List<Object> responseBody = response.getBody();

                if (responseBody == null || responseBody.size() < 2) {
                    log.warn("Неполный или пустой ответ от API World Bank: {}", responseBody);
                    return 0;
                }

                // Второй элемент — список данных по странам
                List<Map<String, Object>> countries = (List<Map<String, Object>>) responseBody.get(1);

                for (Map<String, Object> countryData : countries) {
                    try {
                        if (countryData.containsKey("country") && countryData.containsKey("value")) {
                            Object countryObj = countryData.get("country");
                            String countryName;

                            if (countryObj instanceof Map) {
                                Map<String, Object> countryMap = (Map<String, Object>) countryObj;
                                countryName = (String) countryMap.get("value");
                            } else if (countryObj instanceof String) {
                                countryName = (String) countryObj;
                            } else {
                                log.warn("Unsupported country format: {}", countryObj);
                                continue;
                            }

                            double population = parsePopulation(countryData.get("value"));

                            StatisticRequest request = StatisticRequest.builder()
                                    .title("Population of " + countryName)
                                    .value(population)
                                    .unit("people")
                                    .categoryId(category.getId())
                                    .source("World Bank")
                                    .date(LocalDate.now())
                                    .build();

                            statisticService.createStatistic(request);
                            count++;
                            log.info("Сохранена статистика для {}: {} человек", countryName, population);
                        }
                    } catch (Exception e) {
                        log.error("Ошибка обработки данных страны: {}", countryData, e);
                    }
                }

                log.info("Завершен сбор статистики. Добавлено {} записей", count);
                return count;

            } else {
                log.warn("API вернуло неожиданный статус: {}", response.getStatusCode());
                return 0;
            }

        } catch (RestClientException e) {
            log.error("Ошибка при вызове API", e);
            return 0;
        } catch (Exception e) {
            log.error("Критическая ошибка при сборе статистики", e);
            return 0;
        }
    }


    // Вспомогательный метод для конвертации значения населения
    private double parsePopulation(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
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
            // Получаем или создаём категорию для экономических данных
            Category category = getOrCreateCategory(
                    "Economy", "Economic statistics such as currency exchange rates"
            );

            // Заголовки запроса
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Запрос к Open Exchange Rates API
            ResponseEntity<Map> response = restTemplate.exchange(
                    ECONOMIC_API_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();

                if (data.containsKey("rates")) {
                    Map<String, Object> rates = (Map<String, Object>) data.get("rates");
                    String baseCurrency = (String) data.getOrDefault("base", "USD");

                    for (Map.Entry<String, Object> entry : rates.entrySet()) {
                        try {
                            String currency = entry.getKey();
                            double rate = Double.parseDouble(entry.getValue().toString());

                            StatisticRequest request = StatisticRequest.builder()
                                    .title("Exchange rate: " + currency + " to " + baseCurrency)
                                    .value(rate)
                                    .unit(currency)
                                    .categoryId(category.getId())
                                    .source("Open Exchange Rates")
                                    .date(LocalDate.now())
                                    .build();

                            statisticService.createStatistic(request);
                            count.incrementAndGet();
                            log.info("Сохранён курс: {} = {} {}", currency, rate, baseCurrency);
                        } catch (Exception e) {
                            log.error("Ошибка при обработке валюты: {}", entry.getKey(), e);
                        }
                    }
                } else {
                    log.warn("Ответ API не содержит ключ 'rates': {}", data);
                }
            } else {
                log.warn("API вернул статус: {}", response.getStatusCode());
            }

            log.info("Сбор экономической статистики завершён. Добавлено записей: {}", count.get());
            return count.get();
        } catch (RestClientException e) {
            log.error("Ошибка при получении экономических данных из API", e);
            return 0;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при сборе экономической статистики", e);
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
            Category category = getOrCreateCategory(
                    "Health", "Hospital beds per 1,000 people"
            );

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Получаем JSON как список
            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    HEALTH_API_URL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            log.warn("Raw of response: {}", response.getStatusCode());
            log.warn("Raw of response: {}", response.getBody().get(0));

            if (response.getStatusCode() == HttpStatus.OK) {
                List<Object> fullResponse = response.getBody();

                if (fullResponse.size() >= 2) {
                    List<Map<String, Object>> records = (List<Map<String, Object>>) fullResponse.get(1);

                    for (Map<String, Object> record : records) {
                        try {
                            if (record.containsKey("country") && record.containsKey("value") && record.get("value") != null) {
                                Map<String, Object> countryMap = (Map<String, Object>) record.get("country");
                                String countryName = (String) countryMap.get("value");
                                double beds = Double.parseDouble(record.get("value").toString());

                                StatisticRequest request = StatisticRequest.builder()
                                        .title("Hospital beds in " + countryName)
                                        .value(beds)
                                        .unit("per 1,000 people")
                                        .categoryId(category.getId())
                                        .source("World Bank")
                                        .date(LocalDate.now())
                                        .build();

                                statisticService.createStatistic(request);
                                count.incrementAndGet();
                                log.info("Сохранена статистика по стране {}: {} койки на 1000 человек", countryName, beds);
                            }
                        } catch (Exception e) {
                            log.error("Ошибка при обработке данных: {}", record, e);
                        }
                    }
                } else {
                    log.warn("Ответ от API не содержит данных: {}", fullResponse);
                }
            } else {
                log.info("API вернуло неожиданный статус: {}", response.getStatusCode());
                log.info("API вернуло неожиданный ответ: {}", response.getBody().get(0));
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
            Category category = getOrCreateCategory("Образование", "Статистика об образовании");

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    EDUCATION_API_URL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().size() > 1) {
                Object rawData = response.getBody().get(1);
                if (rawData instanceof List) {
                    List<Map<String, Object>> countries = (List<Map<String, Object>>) rawData;

                    for (Map<String, Object> countryData : countries) {
                        try {
                            Object valueObj = countryData.get("value");
                            if (valueObj == null) {
                                log.warn("Пропущена запись без значения: {}", countryData);
                                continue;
                            }

                            if (!countryData.containsKey("country") || !(countryData.get("country") instanceof Map)) {
                                log.warn("Неверный формат страны: {}", countryData);
                                continue;
                            }

                            Map<String, Object> country = (Map<String, Object>) countryData.get("country");
                            String countryName = (String) country.get("value");
                            double educationSpending = Double.parseDouble(valueObj.toString());

                            StatisticRequest request = StatisticRequest.builder()
                                    .title("Education spending in " + countryName)
                                    .value(educationSpending)
                                    .unit("% of GDP")
                                    .categoryId(category.getId())
                                    .source("World Bank")
                                    .date(LocalDate.now())
                                    .build();

                            statisticService.createStatistic(request);
                            count.incrementAndGet();
                            log.info("Сохранена статистика об образовании для {}: {}% от ВВП", countryName, educationSpending);
                        } catch (Exception e) {
                            log.error("Ошибка при обработке данных об образовании: {}", countryData, e);
                        }
                    }
                }
            } else {
                log.error("API вернуло неожиданный ответ: {}", response.getBody());
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
            // Создаём или получаем категорию
            Category category = getOrCreateCategory("Environment", "CO2 emissions per capita");

            // Заголовки
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Получаем JSON как список
            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    ENVIRONMENT_API_URL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Object> fullResponse = response.getBody();

                if (fullResponse.size() >= 2) {
                    List<Map<String, Object>> records = (List<Map<String, Object>>) fullResponse.get(1);

                    for (Map<String, Object> record : records) {
                        try {
                            if (record.containsKey("country") && record.containsKey("value") && record.get("value") != null) {
                                Map<String, Object> countryMap = (Map<String, Object>) record.get("country");
                                String countryName = (String) countryMap.get("value");
                                double co2Emissions = Double.parseDouble(record.get("value").toString());

                                StatisticRequest request = StatisticRequest.builder()
                                        .title("CO2 emissions in " + countryName)
                                        .value(co2Emissions)
                                        .unit("tons per capita")
                                        .categoryId(category.getId())
                                        .source("World Bank")
                                        .date(LocalDate.now())
                                        .build();

                                statisticService.createStatistic(request);
                                count.incrementAndGet();
                                log.info("Сохранена статистика об экологии для {}: {} тонн CO2 на душу населения", countryName, co2Emissions);
                            }
                        } catch (Exception e) {
                            log.error("Ошибка при обработке данных об экологии для записи: {}", record, e);
                        }
                    }
                } else {
                    log.warn("Ответ API не содержит ожидаемой структуры: {}", fullResponse);
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
     *
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
     *
     * @param name        Название категории
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