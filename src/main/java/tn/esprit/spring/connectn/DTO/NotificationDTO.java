package tn.esprit.spring.connectn.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Long referenceId;
    private String actionUrl;
    private String timeAgo; // Formatted for frontend display
}