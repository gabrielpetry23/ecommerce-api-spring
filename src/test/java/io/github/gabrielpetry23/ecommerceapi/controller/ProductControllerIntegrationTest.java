package io.github.gabrielpetry23.ecommerceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.ProductImage;
import io.github.gabrielpetry23.ecommerceapi.service.EmailService;
import io.github.gabrielpetry23.ecommerceapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
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
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private EmailService emailService;

    private final String PRODUCTS_ENDPOINT = "http://localhost/products";
    private final UUID TEST_PRODUCT_ID = UUID.randomUUID();

    @Test
    void createProduct_ValidInput_ReturnsCreated() throws Exception {
        ProductRequestDTO requestDTO = new ProductRequestDTO("Test Product", "Description", BigDecimal.TEN, 10, UUID.randomUUID());
        Product createdProduct = new Product();
        createdProduct.setId(TEST_PRODUCT_ID);

        when(productService.save(any(Product.class))).thenReturn(createdProduct);

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCTS_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf())
                        .with(jwtForUser(UUID.randomUUID(), "managerUser", "MANAGER")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", PRODUCTS_ENDPOINT + "/" + TEST_PRODUCT_ID));
    }

    @Test
    void getProductById_ValidId_ReturnsOk() throws Exception {
        Product product = new Product();
        product.setId(TEST_PRODUCT_ID);
        product.setName("Test Product");

        when(productService.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCTS_ENDPOINT + "/" + TEST_PRODUCT_ID)
                        .with(jwtForUser(UUID.randomUUID(), "user", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void updateProduct_ValidInput_ReturnsNoContent() throws Exception {
        ProductUpdateDTO updateDTO = new ProductUpdateDTO("Updated Product", "Updated Description", BigDecimal.valueOf(20), 15, UUID.randomUUID());

        mockMvc.perform(MockMvcRequestBuilders.put(PRODUCTS_ENDPOINT + "/" + TEST_PRODUCT_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .with(csrf())
                        .with(jwtForUser(UUID.randomUUID(), "managerUser", "MANAGER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCTS_ENDPOINT + "/" + TEST_PRODUCT_ID)
                        .with(csrf())
                        .with(jwtForUser(UUID.randomUUID(), "adminUser", "ADMIN")))
                .andExpect(status().isNoContent());
    }

    private static RequestPostProcessor jwtForUser(UUID userId, String username, String role) {
        return jwt().jwt(jwt -> jwt
                        .claim("sub", userId.toString())
                        .claim("preferred_username", username))
                .authorities(createAuthorityList(role));
    }
}