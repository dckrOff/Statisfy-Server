package uz.dckroff.statisfy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.model.UserActivity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    Page<UserActivity> findByUser(User user, Pageable pageable);
    
    Page<UserActivity> findByActivityType(String activityType, Pageable pageable);
    
    Page<UserActivity> findByEntityTypeAndEntityId(String entityType, Long entityId, Pageable pageable);
    
    @Query("SELECT ua FROM UserActivity ua WHERE ua.createdAt BETWEEN :startDate AND :endDate")
    Page<UserActivity> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT ua.activityType, COUNT(ua) FROM UserActivity ua GROUP BY ua.activityType ORDER BY COUNT(ua) DESC")
    List<Object[]> countByActivityType();
    
    @Query("SELECT ua.entityType, COUNT(ua) FROM UserActivity ua WHERE ua.entityType IS NOT NULL GROUP BY ua.entityType ORDER BY COUNT(ua) DESC")
    List<Object[]> countByEntityType();
    
    @Query("SELECT FUNCTION('DATE', ua.createdAt) as date, COUNT(ua) FROM UserActivity ua GROUP BY FUNCTION('DATE', ua.createdAt) ORDER BY date DESC")
    List<Object[]> countByDate();
    
    @Query("SELECT COUNT(DISTINCT ua.user) FROM UserActivity ua WHERE ua.createdAt >= :since")
    Long countActiveUsersSince(LocalDateTime since);
} 