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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        log.info("Starting news scraping from kun.uz");
        AtomicInteger savedNewsCount = new AtomicInteger(0);

        try {
            // Create or get default category
            Category defaultCategory = getOrCreateCategory("Uzbekistan News", "Latest news from Uzbekistan");

            // Connect to main news page
            Document mainPage = Jsoup.connect("https://kun.uz/en/news/list")
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .followRedirects(true)
                    .get();

            // Extract news links from main page
            Elements newsLinks = mainPage.select("a[href*='/en/news/']");
            log.info("Found {} potential news links on kun.uz", newsLinks.size());

            // Process each news link (limit to 30 to avoid overwhelming)
            newsLinks.stream()
                    .limit(30)
                    .forEach(linkElement -> {
                        try {
                            String relativeUrl = linkElement.attr("href");
                            if (!relativeUrl.startsWith("/en/news/") || relativeUrl.equals("/en/news/list")) {
                                return;
                            }

                            String fullUrl = "https://kun.uz" + relativeUrl;
                            String linkTitle = linkElement.text().trim();

                            // Skip if title is empty or too short
                            if (linkTitle.isEmpty() || linkTitle.length() < 15) {
                                return;
                            }

                            // Check if news already exists by URL
                            if (newsRepository.existsByTitleAndUrl(linkTitle, fullUrl)) {
                                log.debug("News already exists: {}", linkTitle);
                                return;
                            }

                            // Add small delay to be respectful
                            Thread.sleep(500);

                            // Scrape full article details
                            Document articlePage = Jsoup.connect(fullUrl)
                                    .userAgent(USER_AGENT)
                                    .timeout(TIMEOUT_MS)
                                    .followRedirects(true)
                                    .get();

                            // Extract article title (try multiple selectors)
                            String title = linkTitle;
                            Element titleElement = articlePage.selectFirst("h1.single-header__title");
                            if (titleElement == null) {
                                titleElement = articlePage.selectFirst("h1");
                            }
                            if (titleElement != null && !titleElement.text().trim().isEmpty()) {
                                title = titleElement.text().trim();
                            }

                            // Extract summary/description
                            String summary = "No summary available";
                            Element metaDesc = articlePage.selectFirst("meta[name=description]");
                            if (metaDesc != null && !metaDesc.attr("content").isEmpty()) {
                                summary = metaDesc.attr("content").trim();
                            } else {
                                Element leadElement = articlePage.selectFirst(".single-header__lead");
                                if (leadElement == null) {
                                    leadElement = articlePage.selectFirst(".single-content p");
                                }
                                if (leadElement != null && !leadElement.text().trim().isEmpty()) {
                                    String text = leadElement.text().trim();
                                    summary = text.length() > 300 ? text.substring(0, 300) + "..." : text;
                                }
                            }

                            // Extract content
                            String content = "Content not available";
                            Element contentElement = articlePage.selectFirst(".single-content");
                            if (contentElement != null) {
                                // Remove unwanted elements
                                contentElement.select("script, style, .advertisement, .ads, .social-share").remove();
                                String fullContent = contentElement.text().trim();
                                if (!fullContent.isEmpty()) {
                                    content = fullContent.length() > 2000 ? fullContent.substring(0, 2000) + "..." : fullContent;
                                }
                            }

                            // Extract publish date
                            LocalDateTime publishedDate = LocalDateTime.now();
                            Element dateElement = articlePage.selectFirst(".single-header__date");
                            if (dateElement != null) {
                                String dateText = dateElement.text().trim();
                                LocalDateTime parsedDate = parseKunUzDate(dateText);
                                if (parsedDate != null) {
                                    publishedDate = parsedDate;
                                }
                            }

                            // Determine category from URL
                            String categoryName = determineCategoryFromUrl(relativeUrl);
                            Category articleCategory = getOrCreateCategory(categoryName, "News category: " + categoryName);

                            // Create news entity
                            News news = News.builder()
                                    .title(title)
                                    .summary(summary)
                                    .url(fullUrl)
                                    .source("kun.uz")
                                    .publishedAt(publishedDate)
                                    .category(articleCategory)
                                    .isRelevant(true)
                                    .build();

                            // Save news
                            newsRepository.save(news);
                            savedNewsCount.incrementAndGet();
                            log.info("Saved kun.uz news [{}]: {}", savedNewsCount.get(), title);

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            log.warn("Scraping interrupted");
                            return;
                        } catch (Exception e) {
                            log.error("Error processing kun.uz news item: {}", e.getMessage());
                        }
                    });

            log.info("Completed kun.uz news scraping. Successfully saved {} articles", savedNewsCount.get());
            return savedNewsCount.get();

        } catch (IOException e) {
            log.error("Critical error during kun.uz scraping: {}", e.getMessage(), e);
            return 0;
        } catch (Exception e) {
            log.error("Unexpected error during kun.uz scraping", e);
            return 0;
        }
    }

    /**
     * Parse date from kun.uz format
     */
    private LocalDateTime parseKunUzDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            // Common kun.uz date formats
            String[] patterns = {
                    "HH:mm / dd.MM.yyyy",
                    "dd.MM.yyyy HH:mm",
                    "dd.MM.yyyy",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd"
            };

            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    if (pattern.contains("HH:mm")) {
                        return LocalDateTime.parse(dateStr, formatter);
                    } else {
                        LocalDate date = LocalDate.parse(dateStr, formatter);
                        return date.atStartOfDay();
                    }
                } catch (DateTimeParseException ignored) {
                    // Try next pattern
                }
            }

            // Handle "time / date" format specifically
            if (dateStr.contains("/")) {
                String[] parts = dateStr.split("/");
                if (parts.length == 2) {
                    String timePart = parts[0].trim();
                    String datePart = parts[1].trim();
                    try {
                        LocalDate date = LocalDate.parse(datePart, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        LocalTime time = LocalTime.parse(timePart, DateTimeFormatter.ofPattern("HH:mm"));
                        return LocalDateTime.of(date, time);
                    } catch (DateTimeParseException ignored) {
                        // If time parsing fails, use just the date
                        LocalDate date = LocalDate.parse(datePart, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        return date.atStartOfDay();
                    }
                }
            }

        } catch (Exception e) {
            log.debug("Could not parse kun.uz date: {}", dateStr);
        }

        return null;
    }

    /**
     * Determine category from URL path
     */
    private String determineCategoryFromUrl(String url) {
        if (url.contains("/society/")) return "Society";
        if (url.contains("/politics/")) return "Politics";
        if (url.contains("/economy/")) return "Economy";
        if (url.contains("/sport/")) return "Sport";
        if (url.contains("/culture/")) return "Culture";
        if (url.contains("/world/")) return "World";
        if (url.contains("/tech/")) return "Technology";
        if (url.contains("/health/")) return "Health";

        return "General";
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