//package io.github.gabrielpetry23.ecommerceapi.validators;
//
//import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductDTO;
//import io.github.gabrielpetry23.ecommerceapi.model.Product;
//import io.github.gabrielpetry23.ecommerceapi.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class ProductValidator {
//
//    private final ProductRepository repository;
//
//    public void validarNovoProduto(Product product) {
//        if (existsProductWithTheSameNameAndCategory(product)) {
//            throw new DuplicateRecordExcepcion("Já existe um produto com este nome na mesma categoria.");
//        }
//
//        if (product.getPrice() == null || product.getPrice().doubleValue() <= 0) {
//            throw new InvalidFieldException("price", "O preço deve ser positivo.");
//        }
//
//        if (product.getStock() < 0) {
//            throw new InvalidFieldException("stock", "O estoque não pode ser negativo.");
//        }
//    }
//
//    private boolean existsProductWithTheSameNameAndCategory(Product product) {
//        Optional<Product> produto = repository.findByNameAndCategory(product.getName(), product.getCategory());
//
//        if (product.getId() == null) {
//            return produto.isPresent();
//        }
//
//        return produto
//                .map(Product::getId)
//                .stream()
//                .anyMatch(id -> !id.equals(product.getId()));
//    }
//}
//
