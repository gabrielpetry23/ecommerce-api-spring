package io.github.gabrielpetry23.ecommerceapi.repository;

import io.github.gabrielpetry23.ecommerceapi.model.Notification;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserAndReadAtIsNullOrderByCreatedAtDesc(User user);
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}
