package uz.dckroff.statisfy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.fact.FactResponse;
import uz.dckroff.statisfy.model.Fact;
import uz.dckroff.statisfy.repository.FactRepository;
import uz.dckroff.statisfy.service.FactService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final FactRepository factRepository;
    private final FactService factService;

    @GetMapping("/facts")
    public ResponseEntity<Page<FactResponse>> getAllFacts(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<Fact> facts = factRepository.findAll(pageable);
        return ResponseEntity.ok(facts.map(fact -> FactResponse.builder()
                .id(fact.getId())
                .title(fact.getTitle())
                .content(fact.getContent())
                .category(null) // Lazy loading issue, we need to map manually
                .source(fact.getSource())
                .isPublished(fact.isPublished())
                .createdAt(fact.getCreatedAt())
                .build()));
    }

    @PatchMapping("/facts/{id}/publish")
    public ResponseEntity<FactResponse> publishFact(@PathVariable Long id) {
        Fact fact = factRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fact not found"));
        fact.setPublished(true);
        factRepository.save(fact);
        return ResponseEntity.ok(factService.getFactById(id));
    }

    @PatchMapping("/facts/{id}/unpublish")
    public ResponseEntity<FactResponse> unpublishFact(@PathVariable Long id) {
        Fact fact = factRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fact not found"));
        fact.setPublished(false);
        factRepository.save(fact);
        return ResponseEntity.ok(factService.getFactById(id));
    }
} 