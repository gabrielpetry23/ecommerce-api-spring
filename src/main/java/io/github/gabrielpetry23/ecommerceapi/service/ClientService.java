package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.model.Client;
import io.github.gabrielpetry23.ecommerceapi.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final PasswordEncoder encoder;

    public Client salvar(Client client) {
        var encryptedPassword = encoder.encode(client.getClientSecret());
        client.setClientSecret(encryptedPassword);
        return repository.save(client);
    }

    public Client findByClientId(String clientId) {
        return repository.findByClientId(clientId);
    }
}
