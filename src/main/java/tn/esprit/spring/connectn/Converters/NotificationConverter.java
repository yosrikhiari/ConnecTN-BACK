package tn.esprit.spring.connectn.Converters;



import org.springframework.stereotype.Component;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.NotificationDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Notification;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class NotificationConverter {

    public NotificationDTO toDto(Notification entity) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setType(entity.getType());
        dto.setIsRead(entity.getIsRead());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setReferenceId(entity.getReferenceId());
        dto.setActionUrl(entity.getActionUrl());
        dto.setTimeAgo(getTimeAgo(entity.getCreatedAt()));
        return dto;
    }

    public Notification toEntity(NotificationDTO dto) {
        Notification entity = new Notification();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setTitle(dto.getTitle());
        entity.setMessage(dto.getMessage());
        entity.setType(dto.getType());
        entity.setIsRead(dto.getIsRead());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setReferenceId(dto.getReferenceId());
        entity.setActionUrl(dto.getActionUrl());
        return entity;
    }

    private String getTimeAgo(LocalDateTime dateTime) {
        Duration duration = Duration.between(dateTime, LocalDateTime.now());

        if (duration.toDays() > 0) {
            return duration.toDays() + " days ago";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + " hours ago";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + " minutes ago";
        } else {
            return "Just now";
        }
    }
}