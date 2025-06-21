package uz.dckroff.statisfy.dto.fact;

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
public class FactResponse {
    private Long id;
    private String title;
    private String content;
    private CategoryResponse category;
    private String source;
    private boolean isPublished;
    private LocalDateTime createdAt;
} 