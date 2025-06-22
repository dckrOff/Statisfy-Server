package uz.dckroff.statisfy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.analytics.ActivityCountDTO;
import uz.dckroff.statisfy.dto.analytics.DashboardStatsDTO;
import uz.dckroff.statisfy.dto.analytics.UserActivityDTO;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.service.AnalyticsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/user-stats")
    public ResponseEntity<Page<UserActivityDTO>> getUserStats(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        
        Page<UserActivityDTO> activities = analyticsService.getUserActivities(user, pageable);
        
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/popular-content")
    public ResponseEntity<List<ActivityCountDTO>> getPopularContent(
            @RequestParam String entityType,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Object[]> popularContent = analyticsService.getPopularContent(entityType, limit);
        
        List<ActivityCountDTO> result = popularContent.stream()
                .map(obj -> new ActivityCountDTO(obj[0].toString(), (Long) obj[1]))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/activities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserActivityDTO>> getAllActivities(Pageable pageable) {
        
        Page<UserActivityDTO> activities = analyticsService.getAllActivities(pageable);
        
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/activities/type/{activityType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserActivityDTO>> getActivitiesByType(
            @PathVariable String activityType,
            Pageable pageable) {
        
        Page<UserActivityDTO> activities = analyticsService.getActivitiesByType(activityType, pageable);
        
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/activities/entity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserActivityDTO>> getActivitiesForEntity(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            Pageable pageable) {
        
        Page<UserActivityDTO> activities = analyticsService.getActivitiesForEntity(entityType, entityId, pageable);
        
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/activities/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserActivityDTO>> getActivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        
        Page<UserActivityDTO> activities = analyticsService.getActivitiesByDateRange(startDate, endDate, pageable);
        
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        
        DashboardStatsDTO stats = analyticsService.getDashboardStats();
        
        return ResponseEntity.ok(stats);
    }
} 