package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.dckroff.statisfy.dto.category.CategoryResponse;
import uz.dckroff.statisfy.dto.statistic.StatisticRequest;
import uz.dckroff.statisfy.dto.statistic.StatisticResponse;
import uz.dckroff.statisfy.exception.ResourceNotFoundException;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.model.Statistic;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.repository.StatisticRepository;
import uz.dckroff.statisfy.service.StatisticService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<StatisticResponse> getAllStatistics() {
        return statisticRepository.findAll()
                .stream()
                .map(this::mapToStatisticResponse)
                .collect(Collectors.toList());
    }


    @Override
    public Page<StatisticResponse> getStatisticsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        return statisticRepository.findByCategory(category, pageable)
                .map(this::mapToStatisticResponse);
    }

    @Override
    public StatisticResponse getStatisticById(Long id) {
        Statistic statistic = statisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Statistic not found with id: " + id));
        
        return mapToStatisticResponse(statistic);
    }

    @Override
    public StatisticResponse createStatistic(StatisticRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        Statistic statistic = Statistic.builder()
                .title(request.getTitle())
                .value(request.getValue())
                .unit(request.getUnit())
                .category(category)
                .source(request.getSource())
                .date(request.getDate())
                .build();
        
        Statistic savedStatistic = statisticRepository.save(statistic);
        return mapToStatisticResponse(savedStatistic);
    }

    @Override
    public StatisticResponse updateStatistic(Long id, StatisticRequest request) {
        Statistic statistic = statisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Statistic not found with id: " + id));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        statistic.setTitle(request.getTitle());
        statistic.setValue(request.getValue());
        statistic.setUnit(request.getUnit());
        statistic.setCategory(category);
        statistic.setSource(request.getSource());
        statistic.setDate(request.getDate());
        
        Statistic updatedStatistic = statisticRepository.save(statistic);
        return mapToStatisticResponse(updatedStatistic);
    }

    @Override
    public void deleteStatistic(Long id) {
        if (!statisticRepository.existsById(id)) {
            throw new ResourceNotFoundException("Statistic not found with id: " + id);
        }
        statisticRepository.deleteById(id);
    }

    @Override
    public List<StatisticResponse> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
        return statisticRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::mapToStatisticResponse)
                .collect(Collectors.toList());
    }
    
    private StatisticResponse mapToStatisticResponse(Statistic statistic) {
        return StatisticResponse.builder()
                .id(statistic.getId())
                .title(statistic.getTitle())
                .value(statistic.getValue())
                .unit(statistic.getUnit())
                .category(mapToCategoryResponse(statistic.getCategory()))
                .source(statistic.getSource())
                .date(statistic.getDate())
                .build();
    }
    
    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
} 