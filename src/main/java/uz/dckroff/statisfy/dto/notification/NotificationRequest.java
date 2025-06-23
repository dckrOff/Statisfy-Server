package uz.dckroff.statisfy.dto.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    private String imageUrl;

    private Map<String, String> data;

    @NotNull(message = "User IDs are required")
    private List<Long> userIds;

    private String topic;

    @Builder.Default
    private boolean sendToAll = false;
} 