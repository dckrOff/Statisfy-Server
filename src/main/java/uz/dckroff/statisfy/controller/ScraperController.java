package uz.dckroff.statisfy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.dckroff.statisfy.dto.scraper.ScraperResponse;
import uz.dckroff.statisfy.service.WebScraperService;

@RestController
@RequestMapping("/api/scraper")
@RequiredArgsConstructor
public class ScraperController {

    private final WebScraperService webScraperService;

    @PostMapping("/run-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScraperResponse> scrapeAllSources() {
        try {
            int count = webScraperService.scrapeAllSources();
            return ResponseEntity.ok(ScraperResponse.builder()
                    .articlesScraped(count)
                    .message("Successfully scraped " + count + " articles")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ScraperResponse.builder()
                    .articlesScraped(0)
                    .message("Error scraping sources: " + e.getMessage())
                    .success(false)
                    .build());
        }
    }

    @PostMapping("/kun-uz")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScraperResponse> scrapeKunUz() {
        try {
            int count = webScraperService.scrapeKunUz();
            return ResponseEntity.ok(ScraperResponse.builder()
                    .articlesScraped(count)
                    .message("Successfully scraped " + count + " articles from kun.uz")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ScraperResponse.builder()
                    .articlesScraped(0)
                    .message("Error scraping kun.uz: " + e.getMessage())
                    .success(false)
                    .build());
        }
    }

    @PostMapping("/gazeta-uz")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScraperResponse> scrapeGazetaUz() {
        try {
            int count = webScraperService.scrapeGazetaUz();
            return ResponseEntity.ok(ScraperResponse.builder()
                    .articlesScraped(count)
                    .message("Successfully scraped " + count + " articles from gazeta.uz")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ScraperResponse.builder()
                    .articlesScraped(0)
                    .message("Error scraping gazeta.uz: " + e.getMessage())
                    .success(false)
                    .build());
        }
    }
} 