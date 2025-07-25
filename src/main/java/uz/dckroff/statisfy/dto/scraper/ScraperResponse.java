package uz.dckroff.statisfy.dto.scraper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScraperResponse {
    private int articlesScraped;
    private String message;
    private boolean success;
} 