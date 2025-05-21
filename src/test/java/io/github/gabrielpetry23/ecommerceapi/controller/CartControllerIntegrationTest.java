package io.github.gabrielpetry23.ecommerceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartItemRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserNameIdDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.model.CartItem;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private SecurityService securityService;

    private final String CARTS_ENDPOINT = "http://localhost/carts";
    private final UUID TEST_USER_ID = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
    private final UUID TEST_CART_ID = UUID.fromString("0bc03fcc-eeb6-4e59-93de-f05b9cce6093");

    private User createMockUser(UUID id, String username, String role) {
        User user = new User();
        user.setId(id);
        user.setEmail(username);
        user.setRole(role);
        return user;
    }

    private Cart createMockCart(UUID id, User user) {
        Cart cart = new Cart();
        cart.setId(id);
        cart.setUser(user);
        cart.setTotal(BigDecimal.TEN);
        cart.setItems(Collections.emptyList());
        return cart;
    }

    @Test
    void createCart_ValidInput_ReturnsCreated() throws Exception {
        Cart mockCart = createMockCart(TEST_CART_ID, createMockUser(TEST_USER_ID, "testUser", "USER"));

        when(cartService.createCart()).thenReturn(mockCart);

        mockMvc.perform(MockMvcRequestBuilders.post(CARTS_ENDPOINT)
                        .with(csrf())
                        .with(jwtForUser(TEST_USER_ID, "testUser", "USER")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", CARTS_ENDPOINT + "/" + mockCart.getId()));
    }

    @Test
    void getCartById_AsOwner_ReturnsOk() throws Exception {
        Cart mockCart = createMockCart(TEST_CART_ID, createMockUser(TEST_USER_ID, "testUser", "USER"));
        UserNameIdDTO userDTO = new UserNameIdDTO(mockCart.getUser().getId(), mockCart.getUser().getEmail());
        CartResponseDTO mockCartDTO = new CartResponseDTO(userDTO, Collections.emptyList());

        when(cartService.findById(TEST_CART_ID)).thenReturn(Optional.of(mockCart));

        mockMvc.perform(MockMvcRequestBuilders.get(CARTS_ENDPOINT + "/" + TEST_CART_ID)
                        .with(jwtForUser(TEST_USER_ID, "testUser", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(TEST_USER_ID.toString()));
    }

    @Test
    void addItemToCart_ValidInput_ReturnsCreated() throws Exception {
        CartItemRequestDTO requestDTO = new CartItemRequestDTO(UUID.randomUUID().toString(), 2);
        CartItem mockItem = new CartItem();
        mockItem.setId(UUID.randomUUID());
        mockItem.setCart(createMockCart(TEST_CART_ID, createMockUser(TEST_USER_ID, "testUser", "USER")));

        when(cartService.addItem(any(UUID.class), any(CartItemRequestDTO.class))).thenReturn(mockItem);

        mockMvc.perform(MockMvcRequestBuilders.post(CARTS_ENDPOINT + "/" + TEST_CART_ID + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf())
                        .with(jwtForUser(TEST_USER_ID, "testUser", "USER")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void updateItemQuantity_ValidInput_ReturnsNoContent() throws Exception {
        CartItemRequestDTO requestDTO = new CartItemRequestDTO(UUID.randomUUID().toString(), 5);

        mockMvc.perform(MockMvcRequestBuilders.put(CARTS_ENDPOINT + "/" + TEST_CART_ID + "/items/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf())
                        .with(jwtForUser(TEST_USER_ID, "testUser", "USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void emptyCart_AsOwner_ReturnsNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(CARTS_ENDPOINT + "/" + TEST_CART_ID)
                        .with(csrf())
                        .with(jwtForUser(TEST_USER_ID, "testUser", "USER")))
                .andExpect(status().isNoContent());
    }

    private static RequestPostProcessor jwtForUser(UUID userId, String username, String role) {
        return jwt().jwt(jwt -> jwt
                        .claim("sub", userId.toString())
                        .claim("preferred_username", username))
                .authorities(createAuthorityList(role));
    }
}