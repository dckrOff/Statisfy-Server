package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.dckroff.statisfy.dto.category.CategoryResponse;
import uz.dckroff.statisfy.dto.fact.FactRequest;
import uz.dckroff.statisfy.dto.fact.FactResponse;
import uz.dckroff.statisfy.exception.ResourceNotFoundException;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.model.Fact;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.repository.FactRepository;
import uz.dckroff.statisfy.service.FactService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FactServiceImpl implements FactService {

    private final FactRepository factRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<FactResponse> getAllFacts(Pageable pageable) {
        return factRepository.findByIsPublishedTrue(pageable)
                .map(this::mapToFactResponse);
    }

    @Override
    public Page<FactResponse> getFactsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        return factRepository.findByCategoryAndIsPublishedTrue(category, pageable)
                .map(this::mapToFactResponse);
    }

    @Override
    public FactResponse getFactById(Long id) {
        Fact fact = factRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fact not found with id: " + id));
        
        if (!fact.isPublished()) {
            throw new ResourceNotFoundException("Fact not found with id: " + id);
        }
        
        return mapToFactResponse(fact);
    }

    @Override
    public FactResponse createFact(FactRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        Fact fact = Fact.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .source(request.getSource())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .build();
        
        Fact savedFact = factRepository.save(fact);
        return mapToFactResponse(savedFact);
    }

    @Override
    public FactResponse updateFact(Long id, FactRequest request) {
        Fact fact = factRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fact not found with id: " + id));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        fact.setTitle(request.getTitle());
        fact.setContent(request.getContent());
        fact.setCategory(category);
        fact.setSource(request.getSource());
        
        if (request.getIsPublished() != null) {
            fact.setPublished(request.getIsPublished());
        }
        
        Fact updatedFact = factRepository.save(fact);
        return mapToFactResponse(updatedFact);
    }

    @Override
    public void deleteFact(Long id) {
        if (!factRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fact not found with id: " + id);
        }
        factRepository.deleteById(id);
    }

    @Override
    public List<FactResponse> getRecentFacts() {
        return factRepository.findTop5ByIsPublishedTrueOrderByCreatedAtDesc().stream()
                .map(this::mapToFactResponse)
                .collect(Collectors.toList());
    }
    
    private FactResponse mapToFactResponse(Fact fact) {
        return FactResponse.builder()
                .id(fact.getId())
                .title(fact.getTitle())
                .content(fact.getContent())
                .category(mapToCategoryResponse(fact.getCategory()))
                .source(fact.getSource())
                .isPublished(fact.isPublished())
                .createdAt(fact.getCreatedAt())
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