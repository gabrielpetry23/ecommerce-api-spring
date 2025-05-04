package io.github.gabrielpetry23.ecommerceapi.validators;

import io.github.gabrielpetry23.ecommerceapi.exceptions.DuplicateRecordException;
import io.github.gabrielpetry23.ecommerceapi.exceptions.InvalidFieldException;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductValidator {

    private final ProductRepository repository;

    public void validateNewProduct(Product product) {
        if (product.getCategory() == null) {
            throw new InvalidFieldException("category", "Category is required.");
        }

        if (existsProductWithTheSameNameAndCategory(product)) {
            throw new DuplicateRecordException("Already exists a product with the same name and category.");
        }

        if (product.getPrice() == null || product.getPrice().doubleValue() <= 0) {
            throw new InvalidFieldException("price", "Price must be positive.");
        }

        if (product.getStock() < 0) {
            throw new InvalidFieldException("stock", "Stock must be positive.");
        }
    }

    private boolean existsProductWithTheSameNameAndCategory(Product product) {
        Optional<Product> produto = repository.findByNameAndCategory(product.getName(), product.getCategory());

        if (product.getId() == null) {
            return produto.isPresent();
        }

        return produto
                .map(Product::getId)
                .stream()
                .anyMatch(id -> !id.equals(product.getId()));
    }
}

