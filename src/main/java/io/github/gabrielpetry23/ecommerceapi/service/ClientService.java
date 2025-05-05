package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ClientDTO;
import io.github.gabrielpetry23.ecommerceapi.exceptions.ResourceNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.model.Client;
import io.github.gabrielpetry23.ecommerceapi.repository.ClientRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final PasswordEncoder encoder;

    public Client save(Client client) {
        var encryptedPassword = encoder.encode(client.getClientSecret());
        client.setClientSecret(encryptedPassword);
        return repository.save(client);
    }

    public Optional<Client> findById(UUID id) {
        return repository.findById(id);
    }

    public Client findByClientId(String clientId) {
        return repository.findByClientId(clientId);
    }

    public void update(String id, ClientDTO dto) {

        Optional<Client> existingClient = findById(UUID.fromString(id));
        if (existingClient.isEmpty()) {
            throw new ResourceNotFoundException("Client not found");
        }

        Client client = existingClient.get();

        if (dto.clientId() != null) {
            client.setClientId(dto.clientId());
        }

        if (dto.clientSecret() != null) {
            client.setClientSecret(encoder.encode(dto.clientSecret()));
        }

        if (dto.scope() != null) {
            client.setScope(dto.scope());
        }

        if (dto.redirectURI() != null) {
            client.setRedirectURI(dto.redirectURI());
        }

        repository.save(client);
    }

    public void delete(String id) {
        Optional<Client> existingClient = findById(UUID.fromString(id));

        if (existingClient.isEmpty()) {
            throw new ResourceNotFoundException("Client not found");
        }
        repository.delete(existingClient.get());
    }
}
