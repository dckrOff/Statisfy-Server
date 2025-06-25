package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uz.dckroff.statisfy.config.FactCollectorConfig;
import uz.dckroff.statisfy.dto.fact.FactRequest;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.service.FactCollectorService;
import uz.dckroff.statisfy.service.FactService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Реализация сервиса сбора фактов из внешних источников
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FactCollectorServiceImpl implements FactCollectorService {

    private final RestTemplate restTemplate;
    private final CategoryRepository categoryRepository;
    private final FactService factService;
    private final FactCollectorConfig config;
    private final Random random = new Random();

    // API для получения случайных фактов из Wikipedia
    private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/api/rest_v1/page/random/summary";
    
    // API для получения фактов о числах
    private static final String NUMBERS_API_URL = "http://numbersapi.com/";
    
    // API для получения исторических фактов
    private static final String HISTORICAL_FACTS_API_URL = "https://history.muffinlabs.com/date";
    
    // API для получения научных фактов
    private static final String SCIENCE_FACTS_API_URL = "https://api.spaceflightnewsapi.net/v3/articles";

    @Override
    public int collectWikipediaFacts() {
        log.info("Начало сбора фактов из Wikipedia");
        if (!config.isEnabled()) {
            log.warn("Сбор фактов отключен в настройках");
            return 0;
        }
        
        AtomicInteger count = new AtomicInteger(0);
        int maxFacts = Math.min(config.getMaxFactsPerRun(), 10); // Ограничиваем количество запросов к Wikipedia
        
        try {
            // Получаем или создаем категорию для фактов
            Category category = getOrCreateCategory("Общие знания", "Общие интересные факты");
            
            // Настраиваем заголовки запроса
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Выполняем несколько запросов для получения разных фактов
            for (int i = 0; i < maxFacts; i++) {
                try {
                    // Выполняем запрос к Wikipedia API
                    ResponseEntity<Map> response = restTemplate.exchange(
                            WIKIPEDIA_API_URL,
                            HttpMethod.GET,
                            entity,
                            Map.class
                    );
                    
                    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        Map<String, Object> data = response.getBody();
                        
                        // Проверяем наличие необходимых данных
                        if (data.containsKey("title") && data.containsKey("extract")) {
                            String title = (String) data.get("title");
                            String content = (String) data.get("extract");
                            String source = "Wikipedia";
                            
                            // Создаем объект запроса для сохранения факта
                            FactRequest request = FactRequest.builder()
                                    .title("Факт: " + title)
                                    .content(content)
                                    .categoryId(category.getId())
                                    .source(source)
                                    .isPublished(true)
                                    .build();
                            
                            // Сохраняем факт
                            factService.createFact(request);
                            count.incrementAndGet();
                            log.info("Сохранен факт из Wikipedia: {}", title);
                            
                            // Делаем небольшую паузу между запросами, чтобы не перегружать API
                            Thread.sleep(1000);
                        }
                    }
                } catch (Exception e) {
                    log.error("Ошибка при получении факта из Wikipedia", e);
                }
            }
            
            log.info("Завершен сбор фактов из Wikipedia. Добавлено {} фактов", count.get());
            return count.get();
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сборе фактов из Wikipedia", e);
            return 0;
        }
    }

    @Override
    public int collectNumbersFacts() {
        log.info("Начало сбора фактов о числах");
        if (!config.isEnabled()) {
            log.warn("Сбор фактов отключен в настройках");
            return 0;
        }
        
        AtomicInteger count = new AtomicInteger(0);
        int maxFacts = Math.min(config.getMaxFactsPerRun(), 20);
        
        try {
            // Получаем или создаем категорию для фактов
            Category category = getOrCreateCategory("Математика", "Факты о числах и математике");
            
            // Настраиваем заголовки запроса
            HttpHeaders headers = createHeaders();
            headers.set("Accept", "text/plain");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Выполняем несколько запросов для получения разных фактов
            for (int i = 0; i < maxFacts; i++) {
                try {
                    // Генерируем случайное число от 1 до 1000
                    int randomNumber = random.nextInt(1000) + 1;
                    
                    // Выполняем запрос к Numbers API
                    ResponseEntity<String> response = restTemplate.exchange(
                            NUMBERS_API_URL + randomNumber,
                            HttpMethod.GET,
                            entity,
                            String.class
                    );
                    
                    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        String content = response.getBody();
                        
                        // Создаем объект запроса для сохранения факта
                        FactRequest request = FactRequest.builder()
                                .title("Факт о числе " + randomNumber)
                                .content(content)
                                .categoryId(category.getId())
                                .source("Numbers API")
                                .isPublished(true)
                                .build();
                        
                        // Сохраняем факт
                        factService.createFact(request);
                        count.incrementAndGet();
                        log.info("Сохранен факт о числе {}: {}", randomNumber, content);
                        
                        // Делаем небольшую паузу между запросами
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    log.error("Ошибка при получении факта о числе", e);
                }
            }
            
            log.info("Завершен сбор фактов о числах. Добавлено {} фактов", count.get());
            return count.get();
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сборе фактов о числах", e);
            return 0;
        }
    }

    @Override
    public int collectHistoricalFacts() {
        log.info("Начало сбора исторических фактов");
        if (!config.isEnabled()) {
            log.warn("Сбор фактов отключен в настройках");
            return 0;
        }
        
        AtomicInteger count = new AtomicInteger(0);
        
        try {
            // Получаем или создаем категорию для фактов
            Category category = getOrCreateCategory("История", "Исторические факты и события");
            
            // Настраиваем заголовки запроса
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Выполняем запрос к API исторических фактов
            // API возвращает события, произошедшие в текущий день в истории
            ResponseEntity<Map> response = restTemplate.exchange(
                    HISTORICAL_FACTS_API_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                
                if (data.containsKey("data") && ((Map)data.get("data")).containsKey("Events")) {
                    Map<String, Object> dataMap = (Map<String, Object>) data.get("data");
                    java.util.List<Map<String, Object>> events = (java.util.List<Map<String, Object>>) dataMap.get("Events");
                    
                    // Ограничиваем количество фактов
                    int maxFacts = Math.min(config.getMaxFactsPerRun(), events.size());
                    
                    // Обрабатываем исторические события
                    for (int i = 0; i < maxFacts; i++) {
                        try {
                            Map<String, Object> event = events.get(i);
                            String year = event.get("year").toString();
                            String text = event.get("text").toString();
                            
                            // Создаем объект запроса для сохранения факта
                            FactRequest request = FactRequest.builder()
                                    .title("Историческое событие " + year + " года")
                                    .content(text)
                                    .categoryId(category.getId())
                                    .source("History API")
                                    .isPublished(true)
                                    .build();
                            
                            // Сохраняем факт
                            factService.createFact(request);
                            count.incrementAndGet();
                            log.info("Сохранен исторический факт: {} - {}", year, text);
                        } catch (Exception e) {
                            log.error("Ошибка при обработке исторического события", e);
                        }
                    }
                }
            }
            
            log.info("Завершен сбор исторических фактов. Добавлено {} фактов", count.get());
            return count.get();
        } catch (RestClientException e) {
            log.error("Ошибка при получении исторических фактов из API", e);
            return 0;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сборе исторических фактов", e);
            return 0;
        }
    }

    @Override
    public int collectScienceFacts() {
        log.info("Начало сбора научных фактов");
        if (!config.isEnabled()) {
            log.warn("Сбор фактов отключен в настройках");
            return 0;
        }
        
        AtomicInteger count = new AtomicInteger(0);
        
        try {
            // Получаем или создаем категорию для фактов
            Category category = getOrCreateCategory("Наука", "Научные факты и открытия");
            
            // Настраиваем заголовки запроса
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Выполняем запрос к API научных новостей
            ResponseEntity<Map[]> response = restTemplate.exchange(
                    SCIENCE_FACTS_API_URL + "?_limit=10",
                    HttpMethod.GET,
                    entity,
                    Map[].class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map[] articles = response.getBody();
                
                // Ограничиваем количество фактов
                int maxFacts = Math.min(config.getMaxFactsPerRun(), articles.length);
                
                // Обрабатываем научные статьи
                for (int i = 0; i < maxFacts; i++) {
                    try {
                        Map<String, Object> article = articles[i];
                        String title = (String) article.get("title");
                        String summary = (String) article.get("summary");
                        String source = article.containsKey("newsSite") ? (String) article.get("newsSite") : "Space News";
                        
                        // Создаем объект запроса для сохранения факта
                        FactRequest request = FactRequest.builder()
                                .title(title)
                                .content(summary)
                                .categoryId(category.getId())
                                .source(source)
                                .isPublished(true)
                                .build();
                        
                        // Сохраняем факт
                        factService.createFact(request);
                        count.incrementAndGet();
                        log.info("Сохранен научный факт: {}", title);
                    } catch (Exception e) {
                        log.error("Ошибка при обработке научной статьи", e);
                    }
                }
            }
            
            log.info("Завершен сбор научных фактов. Добавлено {} фактов", count.get());
            return count.get();
        } catch (RestClientException e) {
            log.error("Ошибка при получении научных фактов из API", e);
            return 0;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сборе научных фактов", e);
            return 0;
        }
    }

    @Override
    public int collectAllFacts() {
        log.info("Запуск сбора всех категорий фактов");
        
        if (!config.isEnabled()) {
            log.warn("Сбор фактов отключен в настройках");
            return 0;
        }
        
        int wikipediaCount = collectWikipediaFacts();
        int numbersCount = collectNumbersFacts();
        int historicalCount = collectHistoricalFacts();
        int scienceCount = collectScienceFacts();
        
        int totalCount = wikipediaCount + numbersCount + historicalCount + scienceCount;
        
        log.info("Завершен сбор всех фактов. Всего добавлено {} фактов", totalCount);
        
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