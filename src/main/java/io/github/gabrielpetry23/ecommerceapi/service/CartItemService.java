package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartItemRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.model.CartItem;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final ProductService productService;
    private final CartItemRepository repository;


    public CartItem createCartItem(Cart cart, CartItemRequestDTO dto) {
        Product product = productService.findById(UUID.fromString(dto.productId()))
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(dto.quantity());
        cartItem.setCart(cart);
        repository.save(cartItem);
        return cartItem;
    }

    public Optional<CartItem> findByIdAndCartId(UUID itemId, UUID cartId) {
        return repository.findByIdAndCartId(itemId, cartId);
    }

    public void updateCartItemQuantity(CartItem cartItem, CartItemRequestDTO dto) {
        cartItem.setQuantity(dto.quantity());
        repository.save(cartItem);
    }

    public void delete(CartItem cartItem) {
        repository.delete(cartItem);
    }
}
