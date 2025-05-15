package io.github.gabrielpetry23.ecommerceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartItemRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartItemResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductIdentifierDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserNameIdDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.model.CartItem;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.CartRepository;
import io.github.gabrielpetry23.ecommerceapi.repository.ProductRepository;
import io.github.gabrielpetry23.ecommerceapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    private User testUser;
    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    @Transactional
    void setUp() {
        // Criação de um usuário
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole("USER");
        userRepository.saveAndFlush(testUser);

        // Criação de um carrinho
        testCart = new Cart();
        testCart.setId(UUID.randomUUID());
        testCart.setUser(testUser);
        cartRepository.saveAndFlush(testCart);

        // Criação de um produto
        testProduct = new Product();
        testProduct.setId(UUID.randomUUID());
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.TEN);
        productRepository.saveAndFlush(testProduct);

        // Recarregar entidades para evitar estado "detached"
        testUser = userRepository.findById(testUser.getId()).orElseThrow();
        testCart = cartRepository.findById(testCart.getId()).orElseThrow();
        testProduct = productRepository.findById(testProduct.getId()).orElseThrow();
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createCart_userRole_shouldReturnCreatedWithLocation() throws Exception {
        mockMvc.perform(post("/carts"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getCartById_existingCartForUser_shouldReturnOkWithCartDTO() throws Exception {
        mockMvc.perform(get("/carts/{id}", testCart.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.user.email").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void addItemToCart_validInput_shouldReturnCreatedWithLocation() throws Exception {
        CartItemRequestDTO requestDTO = new CartItemRequestDTO(testProduct.getId().toString(), 2);

        mockMvc.perform(post("/carts/{id}/items", testCart.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updateItemQuantity_validInput_shouldReturnNoContent() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setId(UUID.randomUUID());
        cartItem.setCart(testCart);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(1);
        cartItem.setTotal(BigDecimal.TEN);
        cartRepository.saveAndFlush(testCart);

        CartItemRequestDTO requestDTO = new CartItemRequestDTO(testProduct.getId().toString(), 5);

        mockMvc.perform(put("/carts/{cartId}/items/{itemId}", testCart.getId(), cartItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void deleteItemFromCart_validInput_shouldReturnNoContent() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setId(UUID.randomUUID());
        cartItem.setCart(testCart);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(1);
        cartItem.setTotal(BigDecimal.TEN);
        cartRepository.saveAndFlush(testCart);

        mockMvc.perform(delete("/carts/{cartId}/items/{itemId}", testCart.getId(), cartItem.getId()))
                .andExpect(status().isNoContent());
    }
}