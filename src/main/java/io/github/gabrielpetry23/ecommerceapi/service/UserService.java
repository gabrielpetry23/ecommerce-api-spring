package io.github.gabrielpetry23.ecommerceapi.service;

import aj.org.objectweb.asm.commons.Remapper;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.AddressDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Address;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.AddressRepository;
import io.github.gabrielpetry23.ecommerceapi.repository.UserRepository;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.validators.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final SecurityService securityService;
    private final UserValidator validator;
    private final AddressService addresService;

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

    public void addAddress(User user, AddressDTO dto) {

        if (user.getId() == null) {
            throw new IllegalArgumentException("User must exist to add an address");
        }

        User currentUser = securityService.getCurrentUser();
        validator.validateCurrentUserAccess(user.getId(), currentUser.getId());

        Address createdAddress = addresService.createAddressForUser(user, dto);
        user.getAddresses().add(createdAddress);

        update(user);
    }

    public void validateCurrentUserAccessOrAdmin(UUID userId) {
        validator.validateCurrentUserAccessOrAdmin(userId);
    }
}
