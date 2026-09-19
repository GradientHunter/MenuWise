package com.menuwise.domain.inventory;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double changeAmount;

    @Column(nullable = false, length = 50)
    private String changeType; // DEDUCTION, RESTOCK, ADJUSTMENT

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String reason;
}
