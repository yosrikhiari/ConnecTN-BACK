package tn.esprit.spring.connectn.Entities.CROWDFUNDING;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(indexes = @Index(name = "idx_notification_user", columnList = "userId, isRead, createdAt"))
@Entity
@Getter @Setter
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;  // Recipient of the notification
    private String title; // Short summary ("New Campaign Update")
    private String message; // Detailed message
    private String type; // "MILESTONE", "NEW_UPDATE", "REWARD_UNLOCKED" etc.

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    private Long referenceId; // ID of related entity (campaignId, updateId, etc.)

    // For quick frontend actions
    private String actionUrl; // "/campaigns/123", "/rewards/claim" etc.
}