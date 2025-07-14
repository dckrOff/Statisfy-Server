package uz.dckroff.statisfy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.dckroff.statisfy.dto.statistic.StatisticRequest;
import uz.dckroff.statisfy.dto.statistic.StatisticResponse;

import java.time.LocalDate;
import java.util.List;

public interface StatisticService {
    List<StatisticResponse> getAllStatistics();
    Page<StatisticResponse> getStatisticsByCategory(Long categoryId, Pageable pageable);
    StatisticResponse getStatisticById(Long id);
    StatisticResponse createStatistic(StatisticRequest request);
    StatisticResponse updateStatistic(Long id, StatisticRequest request);
    void deleteStatistic(Long id);
    List<StatisticResponse> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate);
} 