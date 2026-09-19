package com.menuwise.domain.menu;

import com.menuwise.domain.inventory.Ingredient;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemIngredient {

    @EmbeddedId
    private ItemIngredientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantityRequired;
}
