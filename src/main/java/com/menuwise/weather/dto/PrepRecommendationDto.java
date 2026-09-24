package com.menuwise.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing weather-adaptive kitchen prep recommendations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrepRecommendationDto {

    private Long itemId;
    private String itemName;
    private Integer baselineQuantity;
    private Integer recommendedQuantity;
    private Double weatherMultiplier;
    private String rationale;
}
