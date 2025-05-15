package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartItemRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.CartMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.model.CartItem;
import io.github.gabrielpetry23.ecommerceapi.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    @Mock
    private CartMapper cartMapper;

    private void setupRequestContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ServletUriComponentsBuilder.fromPath(""); // Start with an empty path
    }

    @BeforeEach
    void setUp() {
        setupRequestContext(); // Garante que o contexto da requisição esteja configurado antes de cada teste
    }

    @Test
    void createCart_shouldReturnCreatedWithLocation() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Cart createdCart = new Cart();
        createdCart.setId(cartId);

        when(cartService.createCart()).thenReturn(createdCart);

        // Act
        ResponseEntity<Object> response = cartController.createCart();

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        String expectedLocation = "http://localhost/" + cartId.toString();
        assertEquals(expectedLocation, response.getHeaders().get("Location").get(0));
        verify(cartService, times(1)).createCart();
    }

    @Test
    void getById_existingCart_shouldReturnOkWithCartDTO() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        cart.setId(cartId);
        CartResponseDTO cartDTO = new CartResponseDTO(null, null); // Mock DTO

        when(cartService.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartMapper.toDTO(cart)).thenReturn(cartDTO);

        // Act
        ResponseEntity<CartResponseDTO> response = cartController.getById(cartId.toString());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartDTO, response.getBody());
        verify(cartService, times(1)).findById(cartId);
        verify(cartMapper, times(1)).toDTO(cart);
    }

    @Test
    void getById_nonExistingCart_shouldReturnNotFound() {
        // Arrange
        UUID nonExistingCartId = UUID.randomUUID();
        when(cartService.findById(nonExistingCartId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<CartResponseDTO> response = cartController.getById(nonExistingCartId.toString());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(cartService, times(1)).findById(nonExistingCartId);
        verifyNoInteractions(cartMapper);
    }

    @Test
    void addItem_validInput_shouldReturnCreatedWithLocation() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();
        CartItemRequestDTO requestDTO = new CartItemRequestDTO("productId", 2);
        Cart cart = new Cart();
        cart.setId(cartId);
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setCart(cart);

        when(cartService.addItem(cartId, requestDTO)).thenReturn(cartItem);

        // Act
        ResponseEntity<Object> response = cartController.addItem(cartId.toString(), requestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        String expectedLocation = "http://localhost/" + cartId.toString() + "/items/" + cartItemId.toString();
        assertEquals(expectedLocation, response.getHeaders().get("Location").get(0));
        verify(cartService, times(1)).addItem(cartId, requestDTO);
    }

    @Test
    void updateItemQuantity_validInput_shouldReturnNoContent() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        CartItemRequestDTO dto = new CartItemRequestDTO("productId", 3);

        // Act
        ResponseEntity<Object> response = cartController.updateItemQuantity(cartId.toString(), itemId.toString(), dto);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(cartService, times(1)).updateItemQuantity(cartId, itemId, dto);
    }

    @Test
    void emptyCart_shouldReturnNoContent() {
        // Arrange
        UUID cartId = UUID.randomUUID();

        // Act
        ResponseEntity<Object> response = cartController.emptyCart(cartId.toString());

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(cartService, times(1)).emptyCart(cartId);
    }

    @Test
    void deleteItem_shouldReturnNoContent() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        // Act
        ResponseEntity<Object> response = cartController.deleteItem(cartId.toString(), itemId.toString());

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(cartService, times(1)).deleteItem(cartId, itemId);
    }
}