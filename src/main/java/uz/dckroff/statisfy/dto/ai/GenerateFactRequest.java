package uz.dckroff.statisfy.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateFactRequest {

    @NotBlank(message = "Topic is required")
    @Size(min = 2, max = 100, message = "Topic must be between 2 and 100 characters")
    private String topic;
    
    private String language;
} 