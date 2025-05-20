package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.exceptions.InvalidCouponException;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Coupon;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.CouponRepository;
import io.github.gabrielpetry23.ecommerceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void save(Coupon coupon) {
        couponRepository.save(coupon);
        sendNotifications(coupon);
    }

    private void sendNotifications(Coupon coupon) {
        if (coupon.getIsActive()) {
            List<User> allUsers = userRepository.findAll();
            String content;

            if (coupon.getDiscountPercentage() != null && coupon.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                content = String.format("Novo cupom disponível! Use '%s' para %.2f%% de desconto!",
                        coupon.getCode(), coupon.getDiscountPercentage());
            } else if (coupon.getDiscountAmount() != null && coupon.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                content = String.format("Novo cupom disponível! Use '%s' para R$ %.2f de desconto!",
                        coupon.getCode(), coupon.getDiscountAmount());
            } else {
                content = String.format("Novo cupom '%s' disponível! Confira nossas ofertas!", coupon.getCode());
            }

            for (User user : allUsers) {
                if ("USER".equals(user.getRole())) {
                    notificationService.sendAndPersistNotification(user, "NEW_COUPON", content);
                }
            }
        }
    }

    public Coupon findById(UUID id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));
    }

    public Optional<Coupon> findByCode(String code) {
        return couponRepository.findByCode(code);
    }

    public Coupon validateCoupon(String code) {
        return findByCode(code)
                .filter(Coupon::getIsActive)
                .filter(coupon -> coupon.getValidUntil().isAfter(LocalDate.now()))
                .orElseThrow(() -> new InvalidCouponException("Invalid or expired coupon code: " + code));
    }
}