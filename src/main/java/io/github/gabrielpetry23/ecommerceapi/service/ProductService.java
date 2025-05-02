package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.repository.ProductRepository;
import io.github.gabrielpetry23.ecommerceapi.repository.specs.ProductSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

//    private final ProductValidator validator;
    private final ProductRepository repository;

    public Product save(Product product) {
////        validator.validate(product);
//        Usuario currentUser = securityService.getCurrentUser();
//        livro.setUsuario(currentUser);
        return repository.save(product);
    }

    public Optional<Product> findById(UUID id) {
        return repository.findById(id);
    }

    public void update(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product must exist to be updated");
        }
        //validator.validate(product);
        repository.save(product);
    }

    public List<Product> listAll() {
        return repository.findAll();
    }

    public Page<Product> search(String name, String category, String description, BigDecimal price, BigDecimal maxPrice, BigDecimal minPrice, Integer page, Integer pageSize) {

        Specification<Product> specs = Specification.where((root, query, cb) -> cb.conjunction());

        if (name != null) {
            specs = specs.and(ProductSpecs.nameLike(name));
        }
        if (category != null) {
            specs = specs.and(ProductSpecs.categoryEqual(category));
        }
        if (description != null) {
            specs = specs.and(ProductSpecs.descriptionContainsKeywords(description));
        }
        if (price != null) {
            specs = specs.and(ProductSpecs.priceEqual(price));
        }

        if (minPrice != null) {
            specs = specs.and(ProductSpecs.priceGreaterThanOrEqualTo(minPrice));
        }

        if (maxPrice != null) {
            specs = specs.and(ProductSpecs.priceLessThanOrEqualTo(maxPrice));
        }

        return repository.findAll(specs, PageRequest.of(page, pageSize));
    }

    public Page<Product> admSearch(String name, String category, String description, BigDecimal price, BigDecimal maxPrice, BigDecimal minPrice, Integer stock, String createdAt, String updatedAt, UUID id, Integer page, Integer pageSize) {

        Specification<Product> specs = Specification.where((root, query, cb) -> cb.conjunction());

        if (name != null) {
            specs = specs.and(ProductSpecs.nameLike(name));
        }
        if (category != null) {
            specs = specs.and(ProductSpecs.categoryEqual(category));
        }
        if (description != null) {
            specs = specs.and(ProductSpecs.descriptionContainsKeywords(description));
        }
        if (price != null) {
            specs = specs.and(ProductSpecs.priceEqual(price));
        }

        if (minPrice != null) {
            specs = specs.and(ProductSpecs.priceGreaterThanOrEqualTo(minPrice));
        }

        if (maxPrice != null) {
            specs = specs.and(ProductSpecs.priceLessThanOrEqualTo(maxPrice));
        }

        if (stock != null) {
            specs = specs.and(ProductSpecs.stockGreaterThanOrEqualTo(stock));
        }

        if (createdAt != null) {
            specs = specs.and(ProductSpecs.createdAtContains(createdAt.toString()));
        }

        if (updatedAt != null) {
            specs = specs.and(ProductSpecs.updatedAtContains(updatedAt.toString()));
        }

        if (id != null) {
            specs = specs.and(ProductSpecs.idEqual(id));
        }

        return repository.findAll(specs, PageRequest.of(page, pageSize));
    }
}
