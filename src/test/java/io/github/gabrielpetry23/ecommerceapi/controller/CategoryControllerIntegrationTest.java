package io.github.gabrielpetry23.ecommerceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CategoryDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.service.CategoryService;
import io.github.gabrielpetry23.ecommerceapi.service.EmailService;
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

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private SecurityService securityService;

    private final String CATEGORIES_ENDPOINT = "http://localhost/categories";
    private final UUID TEST_CATEGORY_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    private Category createMockCategory(UUID id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    @Test
    void createCategory_ValidInput_ReturnsCreated() throws Exception {
        Category mockCategory = createMockCategory(TEST_CATEGORY_ID, "Electronics");
        CategoryDTO requestDTO = new CategoryDTO(TEST_CATEGORY_ID, "Electronics");

        when(categoryService.save(any())).thenReturn(mockCategory);

        mockMvc.perform(MockMvcRequestBuilders.post(CATEGORIES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf())
                        .with(jwtForUser("testUser", "MANAGER")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", CATEGORIES_ENDPOINT + "/" + mockCategory.getId()));
    }

    @Test
    void listAllCategories_ReturnsOk() throws Exception {
        Category mockCategory = createMockCategory(TEST_CATEGORY_ID, "Electronics");

        when(categoryService.findAll()).thenReturn(Collections.singletonList(mockCategory));

        mockMvc.perform(MockMvcRequestBuilders.get(CATEGORIES_ENDPOINT)
                        .with(jwtForUser("testUser", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_CATEGORY_ID.toString()))
                .andExpect(jsonPath("$[0].name").value("Electronics"));
    }

    @Test
    void getCategoryById_ValidId_ReturnsOk() throws Exception {
        Category mockCategory = createMockCategory(TEST_CATEGORY_ID, "Electronics");

        when(categoryService.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(mockCategory));

        mockMvc.perform(MockMvcRequestBuilders.get(CATEGORIES_ENDPOINT + "/" + TEST_CATEGORY_ID)
                        .with(jwtForUser("testUser", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID.toString()))
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    void updateCategory_ValidInput_ReturnsNoContent() throws Exception {
        CategoryDTO requestDTO = new CategoryDTO(TEST_CATEGORY_ID, "Updated Electronics");

        doNothing().when(categoryService).update(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.put(CATEGORIES_ENDPOINT + "/" + TEST_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf())
                        .with(jwtForUser("testUser", "MANAGER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(categoryService).deleteById(any());

        mockMvc.perform(MockMvcRequestBuilders.delete(CATEGORIES_ENDPOINT + "/" + TEST_CATEGORY_ID)
                        .with(csrf())
                        .with(jwtForUser("testUser", "MANAGER")))
                .andExpect(status().isNoContent());
    }

    private static RequestPostProcessor jwtForUser(String username, String role) {
        return jwt().jwt(jwt -> jwt
                        .claim("preferred_username", username))
                .authorities(createAuthorityList(role));
    }
}