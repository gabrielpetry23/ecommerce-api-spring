package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CouponDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.CouponMapper;
import io.github.gabrielpetry23.ecommerceapi.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController implements GenericController{

    private final CouponService service;
    private final CouponMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> create(@RequestBody CouponDTO couponDTO) {
        var coupon = mapper.toEntity(couponDTO);
        service.save(coupon);
        URI location = generateHeaderLocation(coupon.getId());
        return ResponseEntity.created(location).build();
    }
}
