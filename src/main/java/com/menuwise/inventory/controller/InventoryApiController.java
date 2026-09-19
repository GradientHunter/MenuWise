package com.menuwise.inventory.controller;

import com.menuwise.domain.inventory.Ingredient;
import com.menuwise.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryApiController {

    private final IngredientRepository ingredientRepository;

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(ingredientRepository.findAll());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Ingredient>> getInventoryAlerts() {
        // Flags ingredients where currentStock <= minimumStock or expiryDate is within 48 hours (2 days)
        LocalDate alertHorizon = LocalDate.now().plusDays(2);
        List<Ingredient> alerts = ingredientRepository.findLowStockOrExpiringSoon(alertHorizon);
        return ResponseEntity.ok(alerts);
    }
}
