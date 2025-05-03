package io.github.gabrielpetry23.ecommerceapi.service;

import aj.org.objectweb.asm.commons.Remapper;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public void save(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        repository.save(user);
    }

    public void update(User user) {
        repository.save(user);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    public void delete(User user) {
        repository.delete(user);
    }

    public List<User> findAll() {
        return repository.findAll();
    }
}
