package com.menuwise.ml.service;

import com.menuwise.domain.inventory.Ingredient;
import com.menuwise.domain.inventory.WasteLog;
import com.menuwise.domain.menu.ItemIngredient;
import com.menuwise.domain.order.OrderItem;
import com.menuwise.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataIngestionService {

    private final OrderItemRepository orderItemRepository;
    private final ItemIngredientRepository itemIngredientRepository;
    private final WasteLogRepository wasteLogRepository;
    private final IngredientRepository ingredientRepository;

    /**
     * Computes moving average daily consumption velocity for an ingredient over a given window of days.
     */
    @Transactional(readOnly = true)
    public double calculateMovingAverageConsumption(Long ingredientId, int days) {
        LocalDateTime windowStart = LocalDateTime.now().minusDays(days);
        List<ItemIngredient> bomList = itemIngredientRepository.findByIngredientId(ingredientId);

        if (bomList.isEmpty()) {
            return 0.0;
        }

        double totalConsumed = 0.0;
        for (ItemIngredient bom : bomList) {
            List<OrderItem> orderItems = orderItemRepository.findByItemId(bom.getItem().getId());
            for (OrderItem oi : orderItems) {
                if (oi.getOrder().getOrderTimestamp().isAfter(windowStart)) {
                    totalConsumed += oi.getQuantity() * bom.getQuantityRequired();
                }
            }
        }

        return Math.round((totalConsumed / (double) days) * 100.0) / 100.0;
    }

    /**
     * Returns 7-day and 30-day consumption velocity comparison for all ingredients.
     */
    @Transactional(readOnly = true)
    public Map<String, Map<String, Double>> getIngredientConsumptionVelocities() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (Ingredient ing : ingredients) {
            double ma7 = calculateMovingAverageConsumption(ing.getId(), 7);
            double ma30 = calculateMovingAverageConsumption(ing.getId(), 30);

            Map<String, Double> metrics = new HashMap<>();
            metrics.put("7DayVelocity", ma7);
            metrics.put("30DayVelocity", ma30);
            metrics.put("currentStock", ing.getCurrentStock());
            result.put(ing.getName(), metrics);
        }

        return result;
    }

    /**
     * Summarizes waste logs for ML training sets.
     */
    @Transactional(readOnly = true)
    public List<WasteLog> getRecentWasteLogs(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return wasteLogRepository.findByLogDateBetween(startDate, LocalDate.now());
    }
}
