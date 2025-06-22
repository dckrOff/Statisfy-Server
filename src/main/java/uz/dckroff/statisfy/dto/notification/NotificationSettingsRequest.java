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
public class NotificationSettingsRequest {

    private Boolean dailyFactEnabled;
    
    private Boolean weeklyNewsEnabled;
    
    private Boolean breakingNewsEnabled;
    
    private LocalTime preferredTime;
    
    private String timezone;
    
    private LocalTime quietHoursStart;
    
    private LocalTime quietHoursEnd;
    
    private Boolean quietHoursEnabled;
} 