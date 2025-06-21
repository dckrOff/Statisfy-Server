package uz.dckroff.statisfy.service;

import java.util.List;

public interface WebScraperService {
    /**
     * Scrape news from kun.uz
     * @return Number of new articles scraped
     */
    int scrapeKunUz();
    
    /**
     * Scrape news from gazeta.uz
     * @return Number of new articles scraped
     */
    int scrapeGazetaUz();
    
    /**
     * Run all scrapers
     * @return Total number of new articles scraped
     */
    int scrapeAllSources();
} 