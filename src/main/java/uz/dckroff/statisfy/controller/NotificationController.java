package uz.dckroff.statisfy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.notification.DeviceRegistrationRequest;
import uz.dckroff.statisfy.dto.notification.NotificationRequest;
import uz.dckroff.statisfy.dto.notification.NotificationSettingsRequest;
import uz.dckroff.statisfy.dto.notification.NotificationSettingsResponse;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.service.NotificationService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/register-device")
    public ResponseEntity<Map<String, Boolean>> registerDevice(
            @Valid @RequestBody DeviceRegistrationRequest request,
            @AuthenticationPrincipal User user) {
        
        boolean success = notificationService.registerDevice(request, user);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/unregister")
    public ResponseEntity<Map<String, Boolean>> unregisterDevice(
            @RequestParam String token) {
        
        boolean success = notificationService.unregisterDevice(token);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        
        int sent = notificationService.sendNotification(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", sent > 0);
        response.put("sent", sent);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingsResponse> getNotificationSettings(
            @AuthenticationPrincipal User user) {
        
        NotificationSettingsResponse settings = notificationService.getNotificationSettings(user);
        
        return ResponseEntity.ok(settings);
    }
    
    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingsResponse> updateNotificationSettings(
            @Valid @RequestBody NotificationSettingsRequest request,
            @AuthenticationPrincipal User user) {
        
        NotificationSettingsResponse settings = notificationService.updateNotificationSettings(request, user);
        
        return ResponseEntity.ok(settings);
    }
} 