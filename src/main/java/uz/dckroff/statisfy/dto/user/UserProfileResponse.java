package uz.dckroff.statisfy.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.dckroff.statisfy.model.Role;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
} 