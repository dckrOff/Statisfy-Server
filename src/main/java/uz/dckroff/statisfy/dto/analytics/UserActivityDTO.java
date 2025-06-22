package uz.dckroff.statisfy.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDTO {
    private Long id;
    private Long userId;
    private String username;
    private String activityType;
    private String entityType;
    private Long entityId;
    private String description;
    private LocalDateTime createdAt;
} 