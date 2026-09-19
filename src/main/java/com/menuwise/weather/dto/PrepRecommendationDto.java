package com.menuwise.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrepRecommendationDto {
    private String dishName;
    private int baselinePrepQuantity;
    private int weatherAdjustedPrepQuantity;
    private String rationale;
    private String weatherCondition;
    private double forecastTemperatureCelsius;
}
