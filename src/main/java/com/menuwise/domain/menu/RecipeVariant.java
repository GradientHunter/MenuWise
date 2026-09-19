package com.menuwise.domain.menu;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipe_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private String variantName; // e.g. "Large", "Gluten-Free", "Extra Cheese"

    @Column(nullable = false)
    private Double extraCost;
}
