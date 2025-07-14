package uz.dckroff.statisfy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.statistic.StatisticRequest;
import uz.dckroff.statisfy.dto.statistic.StatisticResponse;
import uz.dckroff.statisfy.service.StatisticService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping
    public ResponseEntity<List<StatisticResponse>> getAllStatistics() {
        return ResponseEntity.ok(statisticService.getAllStatistics());
    }


    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<StatisticResponse>> getStatisticsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(statisticService.getStatisticsByCategory(categoryId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatisticResponse> getStatisticById(@PathVariable Long id) {
        return ResponseEntity.ok(statisticService.getStatisticById(id));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<StatisticResponse>> getStatisticsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(statisticService.getStatisticsByDateRange(startDate, endDate));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatisticResponse> createStatistic(@Valid @RequestBody StatisticRequest request) {
        return ResponseEntity.ok(statisticService.createStatistic(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatisticResponse> updateStatistic(
            @PathVariable Long id,
            @Valid @RequestBody StatisticRequest request
    ) {
        return ResponseEntity.ok(statisticService.updateStatistic(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStatistic(@PathVariable Long id) {
        statisticService.deleteStatistic(id);
        return ResponseEntity.noContent().build();
    }
} 