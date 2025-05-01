package io.github.gabrielpetry23.ecommerceapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Data
@ToString(exclude = {"user"})
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String street;
    private String number;
    private String complement;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}