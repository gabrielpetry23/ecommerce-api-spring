package io.github.gabrielpetry23.ecommerceapi.repository;

import io.github.gabrielpetry23.ecommerceapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductsRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByNameAndCategory(String name, String category);
}
