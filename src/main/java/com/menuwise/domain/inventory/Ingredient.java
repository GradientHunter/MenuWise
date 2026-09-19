package com.menuwise.domain.inventory;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 50)
    private String unit; // kg, L, pcs, grams

    @Column(nullable = false)
    private Double currentStock;

    @Column(nullable = false)
    private Double minimumStock;

    @Column(nullable = false)
    private Double costPerUnit;

    private LocalDate expiryDate;

    @Version
    private Long version; // Optimistic locking for concurrency hardening
}
