package uz.dckroff.statisfy.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.dckroff.statisfy.dto.analytics.ActivityCountDTO;
import uz.dckroff.statisfy.dto.analytics.DashboardStatsDTO;
import uz.dckroff.statisfy.dto.analytics.UserActivityDTO;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.model.UserActivity;
import uz.dckroff.statisfy.repository.*;
import uz.dckroff.statisfy.service.AnalyticsService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserActivityRepository userActivityRepository;
    private final UserRepository userRepository;
    private final FactRepository factRepository;
    private final StatisticRepository statisticRepository;
    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public UserActivity logActivity(User user, String activityType, String entityType, Long entityId, 
                                    String description, HttpServletRequest request) {
        log.info("Logging activity: {} for user: {}", activityType, user != null ? user.getUsername() : "anonymous");
        
        UserActivity activity = UserActivity.builder()
                .user(user)
                .activityType(activityType)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .build();
        
        if (request != null) {
            activity.setIpAddress(getClientIp(request));
            activity.setUserAgent(request.getHeader("User-Agent"));
        }
        
        return userActivityRepository.save(activity);
    }

    @Override
    public Page<UserActivityDTO> getUserActivities(User user, Pageable pageable) {
        log.info("Getting activities for user: {}", user.getUsername());
        
        Page<UserActivity> activities = userActivityRepository.findByUser(user, pageable);
        
        return activities.map(this::mapToDTO);
    }

    @Override
    public Page<UserActivityDTO> getAllActivities(Pageable pageable) {
        log.info("Getting all activities");
        
        Page<UserActivity> activities = userActivityRepository.findAll(pageable);
        
        return activities.map(this::mapToDTO);
    }

    @Override
    public Page<UserActivityDTO> getActivitiesByType(String activityType, Pageable pageable) {
        log.info("Getting activities by type: {}", activityType);
        
        Page<UserActivity> activities = userActivityRepository.findByActivityType(activityType, pageable);
        
        return activities.map(this::mapToDTO);
    }

    @Override
    public Page<UserActivityDTO> getActivitiesForEntity(String entityType, Long entityId, Pageable pageable) {
        log.info("Getting activities for entity: {} with ID: {}", entityType, entityId);
        
        Page<UserActivity> activities = userActivityRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable);
        
        return activities.map(this::mapToDTO);
    }

    @Override
    public Page<UserActivityDTO> getActivitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("Getting activities between {} and {}", startDate, endDate);
        
        Page<UserActivity> activities = userActivityRepository.findByDateRange(startDate, endDate, pageable);
        
        return activities.map(this::mapToDTO);
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        log.info("Getting dashboard statistics");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.toLocalDate().minusDays(now.getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        
        Long totalUsers = userRepository.count();
        Long activeUsersToday = userActivityRepository.countActiveUsersSince(startOfToday);
        Long activeUsersThisWeek = userActivityRepository.countActiveUsersSince(startOfWeek);
        Long activeUsersThisMonth = userActivityRepository.countActiveUsersSince(startOfMonth);
        
        Long totalFacts = factRepository.count();
        Long totalStatistics = statisticRepository.count();
        Long totalNews = newsRepository.count();
        
        List<ActivityCountDTO> activityByType = userActivityRepository.countByActivityType().stream()
                .map(obj -> new ActivityCountDTO((String) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
        
        List<ActivityCountDTO> popularCategories = categoryRepository.findMostPopularCategories().stream()
                .map(obj -> new ActivityCountDTO((String) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
        
        List<ActivityCountDTO> activityByDate = userActivityRepository.countByDate().stream()
                .map(obj -> new ActivityCountDTO(obj[0].toString(), (Long) obj[1]))
                .collect(Collectors.toList());
        
        return DashboardStatsDTO.builder()
                .totalUsers(totalUsers)
                .activeUsersToday(activeUsersToday)
                .activeUsersThisWeek(activeUsersThisWeek)
                .activeUsersThisMonth(activeUsersThisMonth)
                .totalFacts(totalFacts)
                .totalStatistics(totalStatistics)
                .totalNews(totalNews)
                .activityByType(activityByType)
                .popularCategories(popularCategories)
                .activityByDate(activityByDate)
                .build();
    }

    @Override
    public List<Object[]> getPopularContent(String entityType, int limit) {
        log.info("Getting popular content for entity type: {}", entityType);
        
        // This would typically be a more complex query depending on the entity type
        // For now, we'll just return a simple count of activities by entity ID
        return userActivityRepository.findAll().stream()
                .filter(ua -> entityType.equals(ua.getEntityType()) && ua.getEntityId() != null)
                .collect(Collectors.groupingBy(UserActivity::getEntityId, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(e -> new Object[]{e.getKey(), e.getValue()})
                .collect(Collectors.toList());
    }
    
    private UserActivityDTO mapToDTO(UserActivity activity) {
        return UserActivityDTO.builder()
                .id(activity.getId())
                .userId(activity.getUser() != null ? activity.getUser().getId() : null)
                .username(activity.getUser() != null ? activity.getUser().getUsername() : "anonymous")
                .activityType(activity.getActivityType())
                .entityType(activity.getEntityType())
                .entityId(activity.getEntityId())
                .description(activity.getDescription())
                .createdAt(activity.getCreatedAt())
                .build();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 