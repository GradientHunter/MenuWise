package com.menuwise.ml.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpoilageForecastDto {
    private Long ingredientId;
    private String ingredientName;
    private double currentStock;
    private String unit;
    private double projectedDailyConsumption;
    private double projectedSurplusUnits;
    private double spoilageRiskPercentage;
    private double estimatedLossCost;
    private String urgency; // HIGH, MEDIUM, LOW
}
