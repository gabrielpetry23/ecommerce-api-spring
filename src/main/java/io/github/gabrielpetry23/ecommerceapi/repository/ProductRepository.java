package io.github.gabrielpetry23.ecommerceapi.repository;

import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByNameAndCategory(String name, Category category);

    List<Product> findAllByCategory(Category category);
}
