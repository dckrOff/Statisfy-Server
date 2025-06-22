package uz.dckroff.statisfy.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsResponse {

    private Long id;
    
    private Long userId;
    
    private boolean dailyFactEnabled;
    
    private boolean weeklyNewsEnabled;
    
    private boolean breakingNewsEnabled;
    
    private LocalTime preferredTime;
    
    private String timezone;
    
    private LocalTime quietHoursStart;
    
    private LocalTime quietHoursEnd;
    
    private boolean quietHoursEnabled;
} 