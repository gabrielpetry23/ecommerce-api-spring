package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductsService {

//    private final ProductValidator validator;
    private final ProductsRepository repository;

    public Product save(Product product) {
//        validator.validate(product);
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
}
