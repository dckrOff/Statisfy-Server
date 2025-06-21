package uz.dckroff.statisfy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.ai.AIResponse;
import uz.dckroff.statisfy.dto.ai.AnalyzeNewsRequest;
import uz.dckroff.statisfy.dto.ai.GenerateFactRequest;
import uz.dckroff.statisfy.dto.fact.FactResponse;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.service.AIService;


@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/generate-fact")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AIResponse> generateFact(@Valid @RequestBody GenerateFactRequest request) {
        AIResponse response = aiService.generateFact(request.getTopic(), request.getLanguage());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze-news")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AIResponse> analyzeNews(@Valid @RequestBody AnalyzeNewsRequest request) {
        AIResponse response = aiService.analyzeNewsRelevance(request.getNewsId(), request.getUserInterests());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/daily-fact")
    public ResponseEntity<FactResponse> getDailyFact(@AuthenticationPrincipal User user) {
        FactResponse factResponse = aiService.generateDailyFact(user);
        return ResponseEntity.ok(factResponse);
    }
} 