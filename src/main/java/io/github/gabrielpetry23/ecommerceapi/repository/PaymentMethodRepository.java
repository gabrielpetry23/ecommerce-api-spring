package io.github.gabrielpetry23.ecommerceapi.repository;

import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
//    @Query("SELECT a FROM PaymentMethod a WHERE a.user.id = :userId")
//    List<PaymentMethod> findPaymentMethodByUserId(@Param("userId") UUID userId);
    List<PaymentMethod> findAllByUserId(UUID userId);
    Optional<PaymentMethod> findByUserIdAndId(UUID userId, UUID id);
}
