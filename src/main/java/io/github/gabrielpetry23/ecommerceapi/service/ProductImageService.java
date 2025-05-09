package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductImageDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.ProductImage;
import io.github.gabrielpetry23.ecommerceapi.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository repository;

    public ProductImage createImage(Product product, ProductImageDTO imageDto) {
        ProductImage image = new ProductImage();
        if (imageDto.isMain()) {
            image.setMain(true);
//            productService.setMainImageFalse(product);
        }
        image.setImageUrl(imageDto.imageUrl());
        image.setProduct(product);
        repository.save(image);
        return image;
    }

    public void update(ProductImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image must exist to be updated");
        }
        repository.save(image);
    }

    public void delete(ProductImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image must exist to be deleted");
        }
        repository.delete(image);
    }

    public Optional<ProductImage> findById(UUID id) {
        return repository.findById(id);
    }
}
