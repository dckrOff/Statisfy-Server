package uz.dckroff.statisfy.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeNewsRequest {

    @NotNull(message = "News ID is required")
    private Long newsId;
    
    private String userInterests;
} 