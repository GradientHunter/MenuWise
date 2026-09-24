package com.menuwise.ml.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data contract representing predicted ingredient spoilage and risk tier.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpoilageForecastDto {

    private Long ingredientId;
    private String ingredientName;
    private Double currentStock;
    private Double predictedWasteQty;
    private Double spoilageRiskPercentage;
    private String riskTier; // LOW, MEDIUM, HIGH
}
