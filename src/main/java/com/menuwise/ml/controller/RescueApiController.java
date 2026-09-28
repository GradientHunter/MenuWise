package com.menuwise.ml.controller;

import com.menuwise.ml.service.RescueRecipeService;
import com.menuwise.ml.service.WastePredictionService;
import com.menuwise.ml.dto.SpoilageForecastDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API for Rescue Menu engine (Iteration 3 — Person B).
 */
@RestController
@RequestMapping("/api/v1/rescue")
@RequiredArgsConstructor
public class RescueApiController {

    private final RescueRecipeService rescueRecipeService;
    private final WastePredictionService wastePredictionService;
    private final com.menuwise.ml.service.AiRescueChefService aiRescueChefService;

    /**
     * Returns all rescue dish proposals for at-risk ingredients.
     */
    @GetMapping("/proposals")
    public ResponseEntity<List<RescueRecipeService.RescueProposal>> getProposals() {
        return ResponseEntity.ok(rescueRecipeService.generateRescueProposals());
    }

    /**
     * Returns the full spoilage forecast for all ingredients.
     */
    @GetMapping("/spoilage-forecast")
    public ResponseEntity<List<SpoilageForecastDto>> getSpoilageForecast() {
        return ResponseEntity.ok(wastePredictionService.forecastSpoilage());
    }

    /**
     * Returns only at-risk (MEDIUM + HIGH) spoilage forecast entries.
     */
    @GetMapping("/at-risk")
    public ResponseEntity<List<SpoilageForecastDto>> getAtRiskIngredients() {
        return ResponseEntity.ok(wastePredictionService.getAtRiskIngredients());
    }

    /**
     * Returns a summary for the dashboard Rescue Specials card:
     * total at-risk count, HIGH risk count, and estimated waste value (stock × unit cost proxy).
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getRescueSummary() {
        List<SpoilageForecastDto> atRisk = wastePredictionService.getAtRiskIngredients();
        long highCount = atRisk.stream().filter(f -> "HIGH".equals(f.getRiskTier())).count();
        // Estimate potential savings: sum of predictedWasteQty × 120 (avg cost per unit in ৳)
        double estimatedWasteValue = atRisk.stream()
                .mapToDouble(f -> f.getPredictedWasteQty() * 120.0)
                .sum();

        return ResponseEntity.ok(Map.of(
                "atRiskCount",         atRisk.size(),
                "highRiskCount",       highCount,
                "estimatedWasteValue", Math.round(estimatedWasteValue * 100.0) / 100.0
        ));
    }

    /**
     * Synthesizes a new AI-generated dish specifically utilizing at-risk expiring items.
     */
    @GetMapping("/ai-recipe")
    public ResponseEntity<com.menuwise.ml.dto.AiRescueRecipeDto> getAiRecipe() {
        return ResponseEntity.ok(aiRescueChefService.generateRescueRecipe());
    }

    /**
     * Adopts the generated AI recipe into Menu Items and POS catalog.
     */
    @PostMapping("/ai-recipe/adopt")
    public ResponseEntity<com.menuwise.domain.menu.Item> adoptAiRecipe(@RequestBody com.menuwise.ml.dto.AiRescueRecipeDto recipe) {
        return ResponseEntity.ok(aiRescueChefService.adoptRecipeAsSpecial(recipe));
    }
}
