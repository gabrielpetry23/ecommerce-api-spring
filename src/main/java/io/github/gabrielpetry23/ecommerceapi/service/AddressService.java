package io.github.gabrielpetry23.ecommerceapi.service;


import io.github.gabrielpetry23.ecommerceapi.controller.dto.AddressDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.AddressMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Address;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository repository;
    private final AddressMapper mapper;

    public Address createAddressForUser(User user, AddressDTO dto) {
        Address address = new Address();
        address.setStreet(dto.street());
        address.setNumber(dto.number());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setZipCode(dto.zipCode());
        address.setCountry(dto.country());
        address.setUser(user);
        if (dto.complement() != null) {
            address.setComplement(dto.complement());
        }
        return repository.save(address);
    }

    public Optional<Address> findAddressByUserIdAndAddressId(UUID userId, UUID addressId) {
        return repository.findByUserIdAndId(userId, addressId);
    }

    public Address updateAddress(Address address, AddressDTO dto) {
        if (address.getId() == null) {
            throw new IllegalArgumentException("Address must exist to be updated");
        }

        if (dto.street() != null) {
            address.setStreet(dto.street());
        }

        if (dto.number() != null) {
            address.setNumber(dto.number());
        }

        if (dto.city() != null) {
            address.setCity(dto.city());
        }

        if (dto.state() != null) {
            address.setState(dto.state());
        }

        if (dto.zipCode() != null) {
            address.setZipCode(dto.zipCode());
        }

        if (dto.country() != null) {
            address.setCountry(dto.country());
        }

        if (dto.complement() != null) {
            address.setComplement(dto.complement());
        }

        return repository.save(address);
    }

    public void delete(Address address) {
        repository.delete(address);
    }

    public List<AddressDTO> findAllAddressesDTOByUserId(UUID userId) {
        return repository.findAdressesByUserId(userId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public AddressDTO findAddressDTOByUserIdAndAddressId(UUID userId, UUID id) {
        return repository.findByUserIdAndId(userId, id)
                .map(mapper::toDTO)
                .orElse(null);
    }
}
