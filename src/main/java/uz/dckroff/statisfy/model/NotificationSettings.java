package uz.dckroff.statisfy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "notification_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "daily_fact_enabled")
    @Builder.Default
    private boolean dailyFactEnabled = true;

    @Column(name = "weekly_news_enabled")
    @Builder.Default
    private boolean weeklyNewsEnabled = true;

    @Column(name = "breaking_news_enabled")
    @Builder.Default
    private boolean breakingNewsEnabled = true;

    @Column(name = "preferred_time")
    private LocalTime preferredTime;

    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "UTC";

    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;

    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;

    @Column(name = "quiet_hours_enabled")
    @Builder.Default
    private boolean quietHoursEnabled = false;
} 