package io.github.gabrielpetry23.ecommerceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.service.EmailService;
import io.github.gabrielpetry23.ecommerceapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private EmailService emailService;

    private final String USERS_ENDPOINT = "http://localhost/users";
    private final UUID TEST_USER_ID = UUID.randomUUID();

    @Test
    void createUser_ValidInput_ReturnsCreated() throws Exception {
        UserDTO userDTO = new UserDTO("Test User", "test@example.com", "password123", "USER");
        User createdUser = new User();
        createdUser.setId(TEST_USER_ID);

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(TEST_USER_ID);
            return null;
        }).when(userService).save(any(User.class));

        mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .with(csrf())
                        .with(jwtForUser(TEST_USER_ID, "testUser", "USER")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", USERS_ENDPOINT + "/" + TEST_USER_ID));
    }

    @Test
    void getUserById_ValidId_ReturnsOk() throws Exception {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setName("Test User");
        user.setEmail("test@example.com");

        when(userService.findById(TEST_USER_ID)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get(USERS_ENDPOINT + "/" + TEST_USER_ID)
                        .with(jwtForUser(TEST_USER_ID, "adminUser", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUser_ValidInput_ReturnsNoContent() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated User", "updated@example.com", "newPassword123");

        mockMvc.perform(MockMvcRequestBuilders.put(USERS_ENDPOINT + "/" + TEST_USER_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .with(csrf())
                        .with(jwtForUser(TEST_USER_ID, "managerUser", "MANAGER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(USERS_ENDPOINT + "/" + TEST_USER_ID)
                        .with(csrf())
                        .with(jwtForUser(TEST_USER_ID, "adminUser", "ADMIN")))
                .andExpect(status().isNoContent());
    }

    private static RequestPostProcessor jwtForUser(UUID userId, String username, String role) {
        return jwt().jwt(jwt -> jwt
                        .claim("sub", userId.toString())
                        .claim("preferred_username", username))
                .authorities(createAuthorityList(role));
    }
}