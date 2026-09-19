package com.menuwise.weather.service;

import com.menuwise.domain.menu.Item;
import com.menuwise.repository.ItemRepository;
import com.menuwise.weather.dto.PrepRecommendationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherAdaptivePrepService {

    private final ItemRepository itemRepository;

    @Cacheable(value = "weatherForecast")
    public List<PrepRecommendationDto> getDailyPrepRecommendations() {
        log.info("Computing weather-adaptive daily prep recommendations (cache miss)...");

        // Baseline weather simulation/fetch (e.g. cold, rainy day ~10°C)
        double currentTempC = 9.5;
        boolean isRaining = true;
        String condition = isRaining ? "Rain & Cold" : "Mild & Clear";

        List<Item> items = itemRepository.findAll();
        List<PrepRecommendationDto> recommendations = new ArrayList<>();

        for (Item item : items) {
            int baseline = 20; // Default baseline prep servings
            int adjusted = baseline;
            String rationale;

            String lowerName = item.getName().toLowerCase();
            if (lowerName.contains("soup") || lowerName.contains("cocoa") || lowerName.contains("fettuccine")) {
                // Scale up hot dishes & comfort foods by +35% during cold/rain
                adjusted = (int) Math.round(baseline * 1.35);
                rationale = "Scale up +35%: Cold/rainy weather increases demand for hot comfort foods and soups.";
            } else if (lowerName.contains("salad") || lowerName.contains("iced")) {
                // Scale down cold perishables during rainy cold days by -25%
                adjusted = (int) Math.round(baseline * 0.75);
                rationale = "Scale down -25%: Cold & rainy weather decreases demand for chilled drinks and raw salads.";
            } else {
                rationale = "Normal baseline prep: Weather has neutral elasticity on this staple.";
            }

            recommendations.add(PrepRecommendationDto.builder()
                    .dishName(item.getName())
                    .baselinePrepQuantity(baseline)
                    .weatherAdjustedPrepQuantity(adjusted)
                    .rationale(rationale)
                    .weatherCondition(condition)
                    .forecastTemperatureCelsius(currentTempC)
                    .build());
        }

        return recommendations;
    }
}
