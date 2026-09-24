package com.menuwise.ml.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data contract representing daily ingredient consumption records for ML training
 * and synthetic history generation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyConsumptionRecord {

    private LocalDate date;
    private Long ingredientId;
    private Double quantityConsumed;
    private String weatherCondition;
    private Boolean isWeekend;
    private Double rolling7DayAvg;
}
