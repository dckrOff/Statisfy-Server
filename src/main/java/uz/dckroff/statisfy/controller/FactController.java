package uz.dckroff.statisfy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.fact.FactRequest;
import uz.dckroff.statisfy.dto.fact.FactResponse;
import uz.dckroff.statisfy.service.FactService;

import java.util.List;

@RestController
@RequestMapping("/api/facts")
@RequiredArgsConstructor
public class FactController {

    private final FactService factService;

    @GetMapping
    public ResponseEntity<Page<FactResponse>> getAllFacts(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(factService.getAllFacts(pageable));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<FactResponse>> getFactsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(factService.getFactsByCategory(categoryId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactResponse> getFactById(@PathVariable Long id) {
        return ResponseEntity.ok(factService.getFactById(id));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<FactResponse>> getRecentFacts() {
        return ResponseEntity.ok(factService.getRecentFacts());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FactResponse> createFact(@Valid @RequestBody FactRequest request) {
        return ResponseEntity.ok(factService.createFact(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FactResponse> updateFact(
            @PathVariable Long id,
            @Valid @RequestBody FactRequest request
    ) {
        return ResponseEntity.ok(factService.updateFact(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFact(@PathVariable Long id) {
        factService.deleteFact(id);
        return ResponseEntity.noContent().build();
    }
} 