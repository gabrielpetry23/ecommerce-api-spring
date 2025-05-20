package io.github.gabrielpetry23.ecommerceapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_methods")
@Data
@ToString(exclude = {"user"})
@EntityListeners(AuditingEntityListener.class)
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    private User user;

    private String type;
    private String cardNumber;
    private LocalDate expiryDate;
    private String provider;
    private String cardHolderName;
    private String cvv;

    @CreatedDate
    private LocalDateTime createdAt;
}
