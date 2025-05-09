package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartItemRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.CartMapper;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.model.CartItem;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.CartRepository;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.validators.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository repository;
    private final SecurityService securityService;
    private final UserValidator validator;
    private final CartItemService cartItemService;
    private CartMapper mapper;

    public Cart createCart() {

        if (repository.findByUserId(securityService.getCurrentUser().getId()).isPresent()) {
            throw new IllegalArgumentException("User already has a cart.");
        }

        Cart cart = new Cart();
        cart.setUser(securityService.getCurrentUser());
        cart.setItems(new ArrayList<>());
        return repository.save(cart);
    }

    public void validateCartOwnerIsCurrentUser(UUID id) {
        Cart cart = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (!cart.getUser().getId().equals(securityService.getCurrentUser().getId())) {
            throw new AccessDeniedException("Access denied.");
        }
    }

    public Optional<Cart> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Cart> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    public void validateCartOwnerIsCurrentUserOrAdminOrManager(UUID id) {
        Cart cart = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        validator.validateCurrentUserAccessOrAdmin(cart.getUser().getId());
    }

    public CartItem addItem(UUID cartId, CartItemRequestDTO dto) {
//        Cart cart = repository.findById(cartId)
//                .orElseGet(() -> {
//                    Cart newCart = createCart();
//                    validator.validateCurrentUserAccess(newCart.getUser().getId());
//                    return newCart;
//                });
//
//        CartItem cartItem = cartItemService.createCartItem(cart, dto);
//        cart.getItems().add(cartItem);
//        repository.save(cart);
//        return cartItem;
        Cart cart = (cartId != null) ? repository.findById(cartId).orElse(null) : null;

        if (cart == null) {

            User currentUser = securityService.getCurrentUser();

            cart = repository.findByUserId(currentUser.getId())
                    .orElseGet(() -> createCartForUser(currentUser));
        } else {
            validator.validateCurrentUserAccess(cart.getUser().getId());
        }

        CartItem cartItem = cartItemService.createCartItem(cart, dto);
        cart.getItems().add(cartItem);
        repository.save(cart);
        return cartItem;
    }

    private Cart createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        return repository.save(cart);
    }

    public void updateItemQuantity(UUID cartId, UUID itemId, CartItemRequestDTO dto) {
        CartItem cartItem = cartItemService.findByIdAndCartId(itemId, cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        validator.validateCurrentUserAccess(cartItem.getCart().getUser().getId());

        cartItemService.updateCartItemQuantity(cartItem, dto);
    }

    @Transactional
    public void deleteById(UUID id) {
        Cart cart = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
//        validator.validateCurrentUserAccess(cart.getUser().getId());
        repository.delete(cart);
    }

    public void deleteItem(UUID cartId, UUID itemId) {
        Cart cart = repository.findById(cartId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        validator.validateCurrentUserAccess(cart.getUser().getId());

        CartItem cartItem = cartItemService.findByIdAndCartId(itemId, cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        cart.getItems().remove(cartItem);
        repository.save(cart);
        cartItemService.delete(cartItem);
    }

    public CartResponseDTO findCartDTOByUserId(UUID id) {
        Cart cart = repository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        return mapper.toDTO(cart);
    }
}
