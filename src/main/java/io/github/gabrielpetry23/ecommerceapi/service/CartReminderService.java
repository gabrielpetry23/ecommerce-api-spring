package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartReminderService {

    private final CartRepository cartRepository;
    private final NotificationService notificationService;
    private static final int ABANDONED_HOURS = 24;
    private static final int REMINDER_COOLDOWN_HOURS = 48;

    @Scheduled(fixedRate = 21600000, initialDelay = 60000)
    @Transactional
    public void sendCartReminders() {
        LocalDateTime abandonedThreshold = LocalDateTime.now().minusHours(ABANDONED_HOURS);
        LocalDateTime reminderCooldownThreshold = LocalDateTime.now().minusHours(REMINDER_COOLDOWN_HOURS);

        List<Cart> eligibleCarts = cartRepository.findAbandonedCartsEligibleForReminder(
                abandonedThreshold, reminderCooldownThreshold
        );

        System.out.println("Founded " + eligibleCarts.size() + " abandoned carts.");

        for (Cart cart : eligibleCarts) {
            String content = String.format("You left %d item(s) in your cart. Don't lose your choices!", cart.getItems().size());
            notificationService.sendAndPersistNotification(cart.getUser(), "CART_REMINDER", content);
            cart.setLastReminderSentAt(LocalDateTime.now());
            cartRepository.save(cart);
        }
    }
}