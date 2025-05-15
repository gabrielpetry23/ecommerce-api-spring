package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.exceptions.InvalidCouponException;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Coupon;
import io.github.gabrielpetry23.ecommerceapi.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

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