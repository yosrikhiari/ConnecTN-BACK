package tn.esprit.spring.connectn.Services.CROWDFUNDING;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.Converters.NotificationConverter;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.NotificationDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Notification;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.NotificationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationConverter notificationConverter;


    public void createNotification(Long userId, String title, String message,
                                   String type, Long referenceId, String actionUrl) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setActionUrl(actionUrl);

        notificationConverter.toDto(notificationRepository.save(notification));
    }




    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(notificationConverter::toDto)
                .collect(Collectors.toList());
    }

    public Integer getUnreadCount(Long userId) {
        return Math.toIntExact(notificationRepository.countByUserIdAndIsReadFalse(userId));
    }

    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        return notificationConverter.toDto(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }
}