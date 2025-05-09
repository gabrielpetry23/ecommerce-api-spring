package io.github.gabrielpetry23.ecommerceapi.repository;

import org.springframework.data.domain.Page;
import io.github.gabrielpetry23.ecommerceapi.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findAllByUserId(UUID userId, Pageable pageable);
}
