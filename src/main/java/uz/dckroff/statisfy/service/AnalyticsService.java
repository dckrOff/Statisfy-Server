package uz.dckroff.statisfy.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.dckroff.statisfy.dto.analytics.DashboardStatsDTO;
import uz.dckroff.statisfy.dto.analytics.UserActivityDTO;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.model.UserActivity;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsService {

    /**
     * Logs a user activity
     * 
     * @param user the user performing the activity
     * @param activityType the type of activity
     * @param entityType the type of entity involved (optional)
     * @param entityId the ID of the entity involved (optional)
     * @param description a description of the activity
     * @param request the HTTP request
     * @return the created UserActivity
     */
    UserActivity logActivity(User user, String activityType, String entityType, Long entityId, 
                             String description, HttpServletRequest request);
    
    /**
     * Gets user activities for a specific user
     * 
     * @param user the user to get activities for
     * @param pageable pagination information
     * @return a page of user activities
     */
    Page<UserActivityDTO> getUserActivities(User user, Pageable pageable);
    
    /**
     * Gets all user activities
     * 
     * @param pageable pagination information
     * @return a page of user activities
     */
    Page<UserActivityDTO> getAllActivities(Pageable pageable);
    
    /**
     * Gets user activities by activity type
     * 
     * @param activityType the type of activity
     * @param pageable pagination information
     * @return a page of user activities
     */
    Page<UserActivityDTO> getActivitiesByType(String activityType, Pageable pageable);
    
    /**
     * Gets user activities for a specific entity
     * 
     * @param entityType the type of entity
     * @param entityId the ID of the entity
     * @param pageable pagination information
     * @return a page of user activities
     */
    Page<UserActivityDTO> getActivitiesForEntity(String entityType, Long entityId, Pageable pageable);
    
    /**
     * Gets user activities within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return a page of user activities
     */
    Page<UserActivityDTO> getActivitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Gets dashboard statistics for admins
     * 
     * @return dashboard statistics
     */
    DashboardStatsDTO getDashboardStats();
    
    /**
     * Gets popular content based on user activities
     * 
     * @param entityType the type of entity
     * @param limit the maximum number of results to return
     * @return a list of entity IDs and their activity counts
     */
    List<Object[]> getPopularContent(String entityType, int limit);
} 