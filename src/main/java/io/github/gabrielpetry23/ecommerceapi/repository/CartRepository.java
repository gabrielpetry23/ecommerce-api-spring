package io.github.gabrielpetry23.ecommerceapi.repository;

import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserId(UUID userId);
    List<Cart> findByUpdatedAtBeforeAndUserIsNotNull(LocalDateTime twentyFourHoursAgo);
    @Query("SELECT c FROM Cart c WHERE " +
            "c.updatedAt < :abandonedThreshold AND " +
            "c.user IS NOT NULL AND " +
            "(c.lastReminderSentAt IS NULL OR c.lastReminderSentAt < :reminderCooldownThreshold) AND " +
            "SIZE(c.items) > 0")
    List<Cart> findAbandonedCartsEligibleForReminder(
            @Param("abandonedThreshold") LocalDateTime abandonedThreshold,
            @Param("reminderCooldownThreshold") LocalDateTime reminderCooldownThreshold
    );
}
