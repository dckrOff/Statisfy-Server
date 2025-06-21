package uz.dckroff.statisfy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.user.UserPreferenceRequest;
import uz.dckroff.statisfy.dto.user.UserPreferenceResponse;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.service.UserPreferenceService;

@RestController
@RequestMapping("/api/user/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    @PostMapping
    public ResponseEntity<UserPreferenceResponse> savePreferences(
            @Valid @RequestBody UserPreferenceRequest request,
            @AuthenticationPrincipal User user) {
        
        UserPreferenceResponse response = userPreferenceService.saveUserPreference(request, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<UserPreferenceResponse> getPreferences(@AuthenticationPrincipal User user) {
        UserPreferenceResponse response = userPreferenceService.getUserPreference(user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePreferences(@AuthenticationPrincipal User user) {
        userPreferenceService.deleteUserPreference(user.getId());
        return ResponseEntity.noContent().build();
    }
} 