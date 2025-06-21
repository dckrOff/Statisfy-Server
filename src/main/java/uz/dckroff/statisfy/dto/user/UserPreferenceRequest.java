package uz.dckroff.statisfy.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceRequest {
    private String interests;
    private String preferredLanguage;
    private Set<Long> preferredCategoryIds;
} 