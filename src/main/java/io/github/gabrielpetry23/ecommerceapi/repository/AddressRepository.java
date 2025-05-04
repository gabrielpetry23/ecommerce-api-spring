package io.github.gabrielpetry23.ecommerceapi.repository;

import io.github.gabrielpetry23.ecommerceapi.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId")
    List<Address> findAdressesByUserId(@Param("userId") UUID userId);
    Optional<Address> findByUserIdAndId(UUID userId, UUID id);
}
