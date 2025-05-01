package io.github.gabrielpetry23.ecommerceapi.repository;

import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
}
