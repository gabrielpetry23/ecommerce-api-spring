package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.model.Notification;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository repository;

    public void sendAndPersistNotification(User user, String type, String content) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setContent(content);
        repository.save(notification);

        String destination = "/topic/user-notifications/" + user.getId().toString();
        messagingTemplate.convertAndSend(destination, notification);
    }

    public void markNotificationAsRead(UUID notificationId) {
        repository.findById(notificationId).ifPresent(notification -> {
            notification.setReadAt(LocalDateTime.now());
            repository.save(notification);
        });
    }

    public void markAllNotificationsAsReadForUser(User user) {
        List<Notification> unreadNotifications = repository.findByUserAndReadAtIsNullOrderByCreatedAtDesc(user);
        unreadNotifications.forEach(notification -> notification.setReadAt(LocalDateTime.now()));
        repository.saveAll(unreadNotifications);
    }

    public List<Notification> getNotificationsForUser(User user) {
        return repository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getUnreadNotificationsForUser(User user) {
        return repository.findByUserAndReadAtIsNullOrderByCreatedAtDesc(user);
    }
}