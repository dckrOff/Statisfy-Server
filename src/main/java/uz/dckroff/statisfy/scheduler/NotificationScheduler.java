package uz.dckroff.statisfy.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.dckroff.statisfy.service.NotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationService notificationService;

    /**
     * Scheduled task to send daily fact notifications
     * Runs every hour to check for users who have their preferred time set to the current hour
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour at XX:00
    public void scheduleDailyFactNotifications() {
        log.info("Running scheduled daily fact notifications task");
        try {
            notificationService.sendDailyFactNotifications();
        } catch (Exception e) {
            log.error("Error in daily fact notifications scheduler: {}", e.getMessage(), e);
        }
    }

    /**
     * Scheduled task to send weekly news summary
     * Runs every Sunday at 10:00 AM
     */
    @Scheduled(cron = "0 0 10 * * 7") // Every Sunday at 10:00 AM
    public void scheduleWeeklyNewsSummary() {
        log.info("Running scheduled weekly news summary task");
        try {
            notificationService.sendWeeklyNewsSummary();
        } catch (Exception e) {
            log.error("Error in weekly news summary scheduler: {}", e.getMessage(), e);
        }
    }
} 