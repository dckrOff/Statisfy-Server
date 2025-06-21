package uz.dckroff.statisfy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.user.UpdateProfileRequest;
import uz.dckroff.statisfy.dto.user.UserProfileResponse;
import uz.dckroff.statisfy.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }
} 