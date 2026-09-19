package com.menuwise.ml.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RescueRecipeDto {
    private String expiringIngredient;
    private double surplusQuantity;
    private String unit;
    private String recommendedRecipe;
    private double originalPrice;
    private double suggestedDiscountPrice;
    private String discountStrategy;
    private String promotionalCopy;
}
