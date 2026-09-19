package com.menuwise.domain.menu;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemIngredientId implements Serializable {

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "ingredient_id")
    private Long ingredientId;
}
