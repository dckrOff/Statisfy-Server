package uz.dckroff.statisfy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.dckroff.statisfy.model.NotificationSettings;
import uz.dckroff.statisfy.model.User;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    Optional<NotificationSettings> findByUser(User user);
    
    Optional<NotificationSettings> findByUserId(Long userId);
    
    @Query("SELECT ns FROM NotificationSettings ns WHERE ns.dailyFactEnabled = true AND ns.preferredTime = :currentTime")
    List<NotificationSettings> findAllForDailyFactAtTime(LocalTime currentTime);
    
    @Query("SELECT ns FROM NotificationSettings ns WHERE ns.weeklyNewsEnabled = true")
    List<NotificationSettings> findAllForWeeklyNews();
    
    @Query("SELECT ns FROM NotificationSettings ns WHERE ns.breakingNewsEnabled = true")
    List<NotificationSettings> findAllForBreakingNews();
} 