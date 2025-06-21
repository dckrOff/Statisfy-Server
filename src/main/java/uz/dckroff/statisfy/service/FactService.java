package uz.dckroff.statisfy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.dckroff.statisfy.dto.fact.FactRequest;
import uz.dckroff.statisfy.dto.fact.FactResponse;

import java.util.List;

public interface FactService {
    Page<FactResponse> getAllFacts(Pageable pageable);
    Page<FactResponse> getFactsByCategory(Long categoryId, Pageable pageable);
    FactResponse getFactById(Long id);
    FactResponse createFact(FactRequest request);
    FactResponse updateFact(Long id, FactRequest request);
    void deleteFact(Long id);
    List<FactResponse> getRecentFacts();
} 