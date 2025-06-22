package uz.dckroff.statisfy.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalUsers;
    private Long activeUsersToday;
    private Long activeUsersThisWeek;
    private Long activeUsersThisMonth;
    private Long totalFacts;
    private Long totalStatistics;
    private Long totalNews;
    private List<ActivityCountDTO> activityByType;
    private List<ActivityCountDTO> popularCategories;
    private List<ActivityCountDTO> activityByDate;
} 