package com.menuwise.ml.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing an AI-generated culinary dish composed from at-risk expiring ingredients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRescueRecipeDto {

    private String dishName;
    private String tagline;
    private String description;
    private String category;
    private List<IngredientUsage> usedIngredients;
    private Integer estimatedPrepTimeMin;
    private Double estimatedCostPrice;
    private Double suggestedSellingPrice;
    private Double estimatedWasteSavedKg;
    private List<String> preparationSteps;
    private String aiModelUsed;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientUsage {
        private Long ingredientId;
        private String ingredientName;
        private Double quantityPerPortion;
        private String unit;
        private Double currentStock;
        private String expiryUrgency;
    }
}
