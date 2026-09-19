package com.menuwise.domain.inventory;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "waste_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WasteLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantityWasted;

    @Column(nullable = false)
    private Double estimatedCost;

    @Column(nullable = false)
    private LocalDate logDate;

    private String reason; // EXPIRED, SPOILED, PREP_ERROR, DAMAGED
}
