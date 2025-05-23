package io.github.gabrielpetry23.ecommerceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ClientDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Client;
import io.github.gabrielpetry23.ecommerceapi.service.ClientService;
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
public class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private EmailService emailService;

    private final String CLIENTS_ENDPOINT = "http://localhost/clients";
    private final UUID TEST_CLIENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    private Client createMockClient(UUID id, String clientId, String scope) {
        Client client = new Client();
        client.setId(id);
        client.setClientId(clientId);
        client.setScope(scope);
        return client;
    }

    @Test
    void createClient_ValidInput_ReturnsCreated() throws Exception {
        Client mockClient = createMockClient(TEST_CLIENT_ID, "testClient", "read");
        ClientDTO requestDTO = new ClientDTO("testClient", "secret", "http://localhost", "read");

        when(clientService.save(any())).thenReturn(mockClient);

        mockMvc.perform(MockMvcRequestBuilders.post(CLIENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf())
                        .with(jwtForUser("testUser", "MANAGER")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", CLIENTS_ENDPOINT + "/" + mockClient.getId()));
    }

    @Test
    void getClientById_ValidId_ReturnsOk() throws Exception {
        Client mockClient = createMockClient(TEST_CLIENT_ID, "testClient", "read");

        when(clientService.findById(TEST_CLIENT_ID)).thenReturn(Optional.of(mockClient));

        mockMvc.perform(MockMvcRequestBuilders.get(CLIENTS_ENDPOINT + "/" + TEST_CLIENT_ID)
                        .with(jwtForUser("testUser", "MANAGER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value("testClient"))
                .andExpect(jsonPath("$.scope").value("read"));
    }

    @Test
    void updateClient_ValidInput_ReturnsNoContent() throws Exception {
        ClientDTO requestDTO = new ClientDTO("updatedClient", "updatedSecret", "http://localhost/updated", "write");

        doNothing().when(clientService).update(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.put(CLIENTS_ENDPOINT + "/" + TEST_CLIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf())
                        .with(jwtForUser("testUser", "MANAGER")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteClient_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(clientService).delete(any());

        mockMvc.perform(MockMvcRequestBuilders.delete(CLIENTS_ENDPOINT + "/" + TEST_CLIENT_ID)
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