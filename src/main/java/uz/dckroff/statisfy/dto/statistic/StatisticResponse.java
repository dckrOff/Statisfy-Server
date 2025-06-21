package uz.dckroff.statisfy.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.dckroff.statisfy.dto.category.CategoryResponse;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticResponse {
    private Long id;
    private String title;
    private Double value;
    private String unit;
    private CategoryResponse category;
    private String source;
    private LocalDate date;
} 