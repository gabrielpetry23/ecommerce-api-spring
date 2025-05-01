package io.github.gabrielpetry23.ecommerceapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;


@Entity
@Table(name = "cart_items")
@Data
@ToString(exclude = {"cart", "product"})
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
}

