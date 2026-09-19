package com.menuwise.ml.service;

import com.menuwise.domain.inventory.Ingredient;
import com.menuwise.ml.dto.SpoilageForecastDto;
import com.menuwise.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WastePredictionService {

    private final IngredientRepository ingredientRepository;
    private final DataIngestionService dataIngestionService;

    /**
     * Forecasts 7-day spoilage probability and projected volume loss based on velocity and shelf life.
     */
    @Transactional(readOnly = true)
    public List<SpoilageForecastDto> forecast7DaySpoilage() {
        log.info("Computing 7-day ingredient spoilage risk forecast...");
        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<SpoilageForecastDto> forecasts = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (Ingredient ing : ingredients) {
            double velocity = dataIngestionService.calculateMovingAverageConsumption(ing.getId(), 7);
            if (velocity <= 0.05) {
                velocity = 0.5; // Baseline conservative estimation
            }

            long daysUntilExpiry = 7;
            if (ing.getExpiryDate() != null) {
                daysUntilExpiry = Math.max(1, ChronoUnit.DAYS.between(now, ing.getExpiryDate()));
            }

            // Expected consumption before expiry
            double expectedConsumption = velocity * daysUntilExpiry;
            double surplus = Math.max(0.0, ing.getCurrentStock() - expectedConsumption);
            surplus = Math.round(surplus * 10.0) / 10.0;

            double riskPercentage = 0.0;
            String urgency = "LOW";

            if (surplus > 0) {
                riskPercentage = Math.min(100.0, Math.round((surplus / ing.getCurrentStock()) * 100.0));
                urgency = (daysUntilExpiry <= 2 || riskPercentage > 60.0) ? "HIGH" : "MEDIUM";
            }

            double estimatedLoss = Math.round((surplus * ing.getCostPerUnit()) * 100.0) / 100.0;

            forecasts.add(SpoilageForecastDto.builder()
                    .ingredientId(ing.getId())
                    .ingredientName(ing.getName())
                    .currentStock(ing.getCurrentStock())
                    .unit(ing.getUnit())
                    .projectedDailyConsumption(velocity)
                    .projectedSurplusUnits(surplus)
                    .spoilageRiskPercentage(riskPercentage)
                    .estimatedLossCost(estimatedLoss)
                    .urgency(urgency)
                    .build());
        }

        return forecasts;
    }
}
