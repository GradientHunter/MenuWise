package com.menuwise.ml.service;

import com.menuwise.domain.menu.Item;
import com.menuwise.domain.menu.ItemIngredient;
import com.menuwise.ml.dto.RescueRecipeDto;
import com.menuwise.ml.dto.SpoilageForecastDto;
import com.menuwise.repository.ItemIngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RescueRecipeService {

    private final WastePredictionService wastePredictionService;
    private final ItemIngredientRepository itemIngredientRepository;

    /**
     * Identifies items at high spoilage risk and maps them to menu recipes with suggested promotional discounts.
     */
    @Transactional(readOnly = true)
    public List<RescueRecipeDto> generateRescueRecipes() {
        log.info("Generating rescue recipes for at-risk ingredients...");
        List<SpoilageForecastDto> forecasts = wastePredictionService.forecast7DaySpoilage();
        List<RescueRecipeDto> proposals = new ArrayList<>();

        for (SpoilageForecastDto forecast : forecasts) {
            if ("HIGH".equalsIgnoreCase(forecast.getUrgency()) || forecast.getSpoilageRiskPercentage() > 40.0) {
                List<ItemIngredient> boms = itemIngredientRepository.findByIngredientId(forecast.getIngredientId());
                for (ItemIngredient bom : boms) {
                    Item item = bom.getItem();
                    double discountPct = 0.20; // Suggested 20% rescue flash deal
                    double discountedPrice = Math.round((item.getSellingPrice() * (1.0 - discountPct)) * 100.0) / 100.0;

                    proposals.add(RescueRecipeDto.builder()
                            .expiringIngredient(forecast.getIngredientName())
                            .surplusQuantity(forecast.getProjectedSurplusUnits())
                            .unit(forecast.getUnit())
                            .recommendedRecipe(item.getName())
                            .originalPrice(item.getSellingPrice())
                            .suggestedDiscountPrice(discountedPrice)
                            .discountStrategy("Flash 20% Off Happy Hour Special")
                            .promotionalCopy(String.format(
                                    "Chef's Special: Order our %s today for just $%.2f (20%% off)! Fresh batch prepared daily.",
                                    item.getName(), discountedPrice
                            ))
                            .build());
                }
            }
        }

        return proposals;
    }
}
