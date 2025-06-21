package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.model.News;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.repository.NewsRepository;
import uz.dckroff.statisfy.service.WebScraperService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebScraperServiceImpl implements WebScraperService {

    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;
    
    private static final String KUN_UZ_URL = "https://kun.uz/news/category/uzbekiston";
    private static final String GAZETA_UZ_URL = "https://www.gazeta.uz/ru/";
    
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    private static final int TIMEOUT_MS = 10000;

    @Override
    public int scrapeKunUz() {
        log.info("Starting scraping from kun.uz");
        AtomicInteger count = new AtomicInteger(0);
        
        try {
            // Get or create category
            Category category = getOrCreateCategory("Uzbekistan News", "News about Uzbekistan");
            
            // Connect to the website
            Document doc = Jsoup.connect(KUN_UZ_URL)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .get();
            
            // Extract news articles
            Elements newsElements = doc.select("div.news");
            
            newsElements.forEach(element -> {
                try {
                    Element titleElement = element.selectFirst("a.news__title");
                    if (titleElement != null) {
                        String title = titleElement.text();
                        String url = "https://kun.uz" + titleElement.attr("href");
                        
                        // Check if news already exists in database
                        if (!newsRepository.existsByTitleAndUrl(title, url)) {
                            // Extract summary by visiting the article page
                            Document articleDoc = Jsoup.connect(url)
                                    .userAgent(USER_AGENT)
                                    .timeout(TIMEOUT_MS)
                                    .get();
                            
                            Element summaryElement = articleDoc.selectFirst("div.single-content p");
                            Element dateElement = articleDoc.selectFirst(".single-header__date");
                            String summary = summaryElement != null ? summaryElement.text() : "No summary available";
                            
                            LocalDateTime publishDate = LocalDateTime.now();
                            if (dateElement != null) {
                                try {
                                    // Try to parse the date from the website
                                    // Example format: "12:34 / 01.06.2023"
                                    String dateStr = dateElement.text().trim();
                                    if (dateStr.contains("/")) {
                                        dateStr = dateStr.split("/")[1].trim();
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                        publishDate = LocalDateTime.of(
                                                java.time.LocalDate.parse(dateStr, formatter), 
                                                java.time.LocalTime.now());
                                    }
                                } catch (DateTimeParseException e) {
                                    log.warn("Could not parse date from kun.uz: {}", dateElement.text(), e);
                                }
                            }
                            
                            // Create and save news
                            News news = News.builder()
                                    .title(title)
                                    .summary(summary)
                                    .url(url)
                                    .source("kun.uz")
                                    .publishedAt(publishDate)
                                    .category(category)
                                    .isRelevant(true)
                                    .build();
                            
                            newsRepository.save(news);
                            count.incrementAndGet();
                            log.info("Saved news: {}", title);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error processing kun.uz news item", e);
                }
            });
            
            log.info("Completed scraping from kun.uz. Added {} new articles", count.get());
            return count.get();
        } catch (IOException e) {
            log.error("Error scraping kun.uz", e);
            return 0;
        }
    }

    @Override
    public int scrapeGazetaUz() {
        log.info("Starting scraping from gazeta.uz");
        AtomicInteger count = new AtomicInteger(0);
        
        try {
            // Get or create category
            Category category = getOrCreateCategory("Gazeta News", "News from gazeta.uz");
            
            // Connect to the website
            Document doc = Jsoup.connect(GAZETA_UZ_URL)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .get();
            
            // Extract news articles
            Elements newsElements = doc.select("div.nblock");
            
            newsElements.forEach(element -> {
                try {
                    Element titleElement = element.selectFirst("a.ntitle");
                    if (titleElement != null) {
                        String title = titleElement.text();
                        String url = "https://www.gazeta.uz" + titleElement.attr("href");
                        
                        // Check if news already exists in database
                        if (!newsRepository.existsByTitleAndUrl(title, url)) {
                            // Extract summary
                            Element summaryElement = element.selectFirst("p.ntext");
                            String summary = summaryElement != null ? summaryElement.text() : "No summary available";
                            
                            // Extract date if available
                            Element dateElement = element.selectFirst("p.ndate");
                            LocalDateTime publishDate = LocalDateTime.now();
                            if (dateElement != null) {
                                try {
                                    // Try to parse the date from the website
                                    // Example format: "15 июня 2023, 14:30"
                                    String dateStr = dateElement.text().trim();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("ru"));
                                    publishDate = LocalDateTime.parse(dateStr, formatter);
                                } catch (DateTimeParseException e) {
                                    log.warn("Could not parse date from gazeta.uz: {}", dateElement.text(), e);
                                }
                            }
                            
                            // Create and save news
                            News news = News.builder()
                                    .title(title)
                                    .summary(summary)
                                    .url(url)
                                    .source("gazeta.uz")
                                    .publishedAt(publishDate)
                                    .category(category)
                                    .isRelevant(true)
                                    .build();
                            
                            newsRepository.save(news);
                            count.incrementAndGet();
                            log.info("Saved news: {}", title);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error processing gazeta.uz news item", e);
                }
            });
            
            log.info("Completed scraping from gazeta.uz. Added {} new articles", count.get());
            return count.get();
        } catch (IOException e) {
            log.error("Error scraping gazeta.uz", e);
            return 0;
        }
    }

    @Override
    public int scrapeAllSources() {
        int kunUzCount = scrapeKunUz();
        int gazetaUzCount = scrapeGazetaUz();
        
        return kunUzCount + gazetaUzCount;
    }
    
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