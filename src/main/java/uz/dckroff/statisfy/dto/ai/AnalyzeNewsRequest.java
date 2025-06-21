package uz.dckroff.statisfy.dto.ai;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeNewsRequest {

    @NotNull(message = "News ID is required")
    private Long newsId;
    
    private String userInterests;
} 