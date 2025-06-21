package uz.dckroff.statisfy.dto.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.dckroff.statisfy.dto.category.CategoryResponse;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private Long id;
    private String title;
    private String summary;
    private String url;
    private String source;
    private LocalDateTime publishedAt;
    private CategoryResponse category;
    private boolean isRelevant;
} 