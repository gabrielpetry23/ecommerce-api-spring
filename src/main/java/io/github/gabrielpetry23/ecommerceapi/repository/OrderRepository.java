package io.github.gabrielpetry23.ecommerceapi.repository;

import org.springframework.data.domain.Page;
import io.github.gabrielpetry23.ecommerceapi.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findAllByUserId(UUID userId, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.items oi JOIN FETCH oi.product WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") UUID id);
}
