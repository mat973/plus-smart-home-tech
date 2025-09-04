package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "shopping_carts")
@Getter
@Setter
@Builder
public class Cart {

    @Id
    @GeneratedValue
    private UUID cartId;

    @Column(nullable = false)
    private String username;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CartItem> items;

    @Column(nullable = false)
    private boolean active = true;
}

