package uz.dckroff.statisfy.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.dckroff.statisfy.service.WebScraperService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScraperScheduler {

    private final WebScraperService webScraperService;

    /**
     * Run web scrapers every 3 hours
     */
    @Scheduled(fixedRate = 10800000) // 3 hours
    public void runScrapers() {
        log.info("Starting scheduled web scraping");
        try {
            int count = webScraperService.scrapeAllSources();
            log.info("Scheduled web scraping completed. Scraped {} articles", count);
        } catch (Exception e) {
            log.error("Error during scheduled web scraping", e);
        }
    }
} 