package com.menuwise.ml.service;

import com.menuwise.domain.inventory.Ingredient;
import com.menuwise.domain.menu.Item;
import com.menuwise.domain.menu.ItemIngredient;
import com.menuwise.ml.dto.SpoilageForecastDto;
import com.menuwise.repository.IngredientRepository;
import com.menuwise.repository.ItemIngredientRepository;
import com.menuwise.repository.ItemRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Rescue Recipe Engine (Iteration 3 — Person B).
 * Matches at-risk ingredients to menu items that use them, proposing
 * priority dishes + a promotional discount to drive depletion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RescueRecipeService {

    private final WastePredictionService wastePredictionService;
    private final ItemIngredientRepository itemIngredientRepository;
    private final ItemRepository itemRepository;
    private final IngredientRepository ingredientRepository;

    /**
     * Generates rescue dish proposals for all at-risk ingredients.
     */
    public List<RescueProposal> generateRescueProposals() {
        List<SpoilageForecastDto> atRisk = wastePredictionService.getAtRiskIngredients();
        List<RescueProposal> proposals = new ArrayList<>();

        for (SpoilageForecastDto forecast : atRisk) {
            Ingredient ingredient = ingredientRepository.findById(forecast.getIngredientId()).orElse(null);
            if (ingredient == null) continue;

            // Find items that use this ingredient via BOM
            List<ItemIngredient> boms = itemIngredientRepository.findByIngredientId(ingredient.getId());
            List<Item> matchedItems = boms.stream()
                    .map(ItemIngredient::getItem)
                    .distinct()
                    .toList();

            if (matchedItems.isEmpty()) continue;

            double discountPct = computeRescueDiscount(forecast.getRiskTier());
            String urgencyLabel = buildUrgencyLabel(ingredient, forecast);

            for (Item item : matchedItems) {
                double originalPrice = item.getSellingPrice();
                double rescuePrice = Math.round(originalPrice * (1.0 - discountPct / 100.0) * 100.0) / 100.0;

                proposals.add(RescueProposal.builder()
                        .ingredientId(ingredient.getId())
                        .ingredientName(ingredient.getName())
                        .currentStock(ingredient.getCurrentStock())
                        .expiryDate(ingredient.getExpiryDate())
                        .riskTier(forecast.getRiskTier())
                        .spoilageRiskPercentage(forecast.getSpoilageRiskPercentage())
                        .suggestedItemId(item.getId())
                        .suggestedItemName(item.getName())
                        .originalPrice(originalPrice)
                        .rescuePrice(rescuePrice)
                        .discountPercentage(discountPct)
                        .urgencyMessage(urgencyLabel)
                        .build());
            }
        }

        log.info("Generated {} rescue proposals for at-risk ingredients", proposals.size());
        return proposals;
    }

    private double computeRescueDiscount(String riskTier) {
        return switch (riskTier) {
            case "HIGH"   -> 25.0;
            case "MEDIUM" -> 12.0;
            default       -> 5.0;
        };
    }

    private String buildUrgencyLabel(Ingredient ingredient, SpoilageForecastDto forecast) {
        if (ingredient.getExpiryDate() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), ingredient.getExpiryDate());
            if (days <= 0) return "EXPIRED — Discard immediately";
            if (days == 1) return "Expires TOMORROW — Sell today";
            if (days <= 3) return "Expires in " + days + " days — Prioritize now";
            return "Expires in " + days + " days — Use soon";
        }
        return "Low stock alert — " + forecast.getRiskTier() + " risk";
    }

    @Data
    @Builder
    public static class RescueProposal {
        private Long ingredientId;
        private String ingredientName;
        private Double currentStock;
        private LocalDate expiryDate;
        private String riskTier;
        private Double spoilageRiskPercentage;
        private Long suggestedItemId;
        private String suggestedItemName;
        private Double originalPrice;
        private Double rescuePrice;
        private Double discountPercentage;
        private String urgencyMessage;
    }
}
