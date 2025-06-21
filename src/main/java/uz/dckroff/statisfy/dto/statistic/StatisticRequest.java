package uz.dckroff.statisfy.dto.statistic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    
    @NotNull(message = "Value is required")
    private Double value;
    
    private String unit;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private String source;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
} 