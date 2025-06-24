package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.dckroff.statisfy.dto.category.CategoryResponse;
import uz.dckroff.statisfy.dto.news.NewsResponse;
import uz.dckroff.statisfy.dto.newsapi.NewsApiResponse;
import uz.dckroff.statisfy.exception.ResourceNotFoundException;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.model.News;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.repository.NewsRepository;
import uz.dckroff.statisfy.service.NewsApiService;
import uz.dckroff.statisfy.service.NewsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;
    private final NewsApiService newsApiService;

    private static final Map<String, String> CATEGORY_MAPPING = new HashMap<>();

    static {
        CATEGORY_MAPPING.put("technology", "Technology");
        CATEGORY_MAPPING.put("science", "Science");
        CATEGORY_MAPPING.put("health", "Health");
        CATEGORY_MAPPING.put("business", "Business");
        CATEGORY_MAPPING.put("entertainment", "Entertainment");
        CATEGORY_MAPPING.put("sports", "Sports");
        CATEGORY_MAPPING.put("general", "General");
    }

    @Override
    @Cacheable(value = "newsCache", key = "'allNews' + #pageable.pageNumber + #pageable.pageSize")
    public Page<NewsResponse> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable)
                .map(this::mapToNewsResponse);
    }

    @Override
    @Cacheable(value = "newsCache", key = "'newsByCategory' + #categoryId + #pageable.pageNumber + #pageable.pageSize")
    public Page<NewsResponse> getNewsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        return newsRepository.findByCategory(category, pageable)
                .map(this::mapToNewsResponse);
    }

    @Override
    @Cacheable(value = "newsCache", key = "'relevantNews' + #pageable.pageNumber + #pageable.pageSize")
    public Page<NewsResponse> getRelevantNews(Pageable pageable) {
        return newsRepository.findByIsRelevantTrue(pageable)
                .map(this::mapToNewsResponse);
    }

    @Override
    @Cacheable(value = "newsCache", key = "'news' + #id")
    public NewsResponse getNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found with id: " + id));

        return mapToNewsResponse(news);
    }

    @Override
    @Scheduled(fixedRate = 1800000) // 30 minutes
    public void fetchNewsFromExternalApi() {
        log.info("Fetching news from external API");
        
        for (Map.Entry<String, String> entry : CATEGORY_MAPPING.entrySet()) {
            try {
                String apiCategory = entry.getKey();
                String localCategory = entry.getValue();
                
                // Fetch news from API
                NewsApiResponse response = newsApiService.fetchTopHeadlines(apiCategory, "ru");
                
                if (response != null && "ok".equalsIgnoreCase(response.getStatus())) {
                    // Find or create category
                    Category category = categoryRepository.findByName(localCategory)
                            .orElseGet(() -> {
                                Category newCategory = Category.builder()
                                        .name(localCategory)
                                        .description(localCategory + " news")
                                        .build();
                                return categoryRepository.save(newCategory);
                            });
                    
                    // Process articles
                    response.getArticles().forEach(article -> {
                        // Check if news already exists
                        if (!newsRepository.existsByTitleAndUrl(article.getTitle(), article.getUrl())) {
                            News news = News.builder()
                                    .title(article.getTitle())
                                    .summary(article.getDescription() != null ? article.getDescription() : "No description available")
                                    .url(article.getUrl())
                                    .source(article.getSource().getName())
                                    .publishedAt(parseDateTime(article.getPublishedAt()))
                                    .category(category)
                                    .isRelevant(true) // Default to true, can be updated later
                                    .build();
                            
                            newsRepository.save(news);
                        }
                    });
                    
                    log.info("Successfully fetched and saved {} news for category: {}", 
                            response.getArticles().size(), localCategory);
                }
            } catch (Exception e) {
                log.error("Error fetching news for category: " + entry.getValue(), e);
            }
        }
    }

    @Override
    @Cacheable(value = "newsCache", key = "'latestNews'")
    public List<NewsResponse> getLatestNews() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        return newsRepository.findByPublishedAtAfterOrderByPublishedAtDesc(oneDayAgo).stream()
                .map(this::mapToNewsResponse)
                .collect(Collectors.toList());
    }
    
    private NewsResponse mapToNewsResponse(News news) {
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .summary(news.getSummary())
                .url(news.getUrl())
                .source(news.getSource())
                .publishedAt(news.getPublishedAt() != null ? 
                    news.getPublishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .category(mapToCategoryResponse(news.getCategory()))
                .isRelevant(news.isRelevant())
                .build();
    }
    
    private CategoryResponse mapToCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
    
    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.error("Error parsing date: " + dateTimeStr, e);
            return LocalDateTime.now();
        }
    }
} 