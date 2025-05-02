package io.github.gabrielpetry23.ecommerceapi.repository.specs;

import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductSpecs {

    public static Specification<Product> nameLike(String name) {
        return (root, query, cb) ->
                cb.like(cb.upper(root.get("name")), "%" + name.toUpperCase() + "%");
    }

    public static Specification<Product> categoryNameEqual(String categoryName) {
        return (root, query, cb) -> {
            Join<Product, Category> categoryJoin = root.join("category");
            return cb.equal(cb.upper(categoryJoin.get("name")), categoryName.toUpperCase());
        };
    }

    public static Specification<Product> categoryIdEqual(UUID categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> descriptionContainsKeywords(String description) {
        return (root, query, cb) -> {
            if (description == null || description.trim().isEmpty()) return null;

            String[] words = description.trim().toUpperCase().split("\\s+");

            List<Predicate> predicates = new ArrayList<>();
            for (String word : words) {
                predicates.add(cb.like(cb.upper(root.get("description")), "%" + word + "%"));
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> priceEqual(BigDecimal price) {
        return (root, query, cb) ->
                cb.equal(root.get("price"), price);
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(BigDecimal minPrice) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> priceLessThanOrEqualTo(BigDecimal maxPrice) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> stockGreaterThanOrEqualTo(Integer stock) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("stock"), stock);
    }

    public static Specification<Product> idEqual(UUID id) {
        return (root, query, cb) ->
                cb.equal(root.get("id"), id);
    }

    public static Specification<Product> createdAtContains(String partialDate) {
        return dateContains("createdAt", partialDate);
    }

    public static Specification<Product> updatedAtContains(String partialDate) {
        return dateContains("updatedAt", partialDate);
    }

    private static Specification<Product> dateContains(String field, String partialDate) {
        return (root, query, cb) -> {
            Expression<String> formattedDate = cb.function(
                    "to_char",
                    String.class,
                    root.get(field),
                    cb.literal("YYYY-MM-DD")
            );
            return cb.like(formattedDate, partialDate + "%");
        };
    }
}

