package uz.dckroff.statisfy.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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