package com.menuwise.ml.controller;

import com.menuwise.ml.dto.RescueRecipeDto;
import com.menuwise.ml.dto.SpoilageForecastDto;
import com.menuwise.ml.service.RescueRecipeService;
import com.menuwise.ml.service.WastePredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/waste")
@RequiredArgsConstructor
public class WasteApiController {

    private final WastePredictionService wastePredictionService;
    private final RescueRecipeService rescueRecipeService;

    @GetMapping("/forecast")
    public ResponseEntity<List<SpoilageForecastDto>> getWasteForecast() {
        return ResponseEntity.ok(wastePredictionService.forecast7DaySpoilage());
    }

    @GetMapping("/rescue-recipes")
    public ResponseEntity<List<RescueRecipeDto>> getRescueRecipes() {
        return ResponseEntity.ok(rescueRecipeService.generateRescueRecipes());
    }
}
