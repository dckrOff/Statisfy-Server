package uz.dckroff.statisfy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDto {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
