package com.menuwise.ml.service;

import com.menuwise.domain.menu.Item;
import com.menuwise.repository.ItemRepository;
import com.menuwise.repository.OrderRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * What-If Pricing Elasticity Simulator (Iteration 4 — Person B).
 * Models demand response to price changes using price elasticity of demand (PED).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PricingSimulatorService {

    private final ItemRepository itemRepository;

    private static final double DEFAULT_ELASTICITY = -1.2; // price-elastic restaurant demand
    private static final int DEFAULT_BASELINE_DEMAND = 120;

    /**
     * Simulates the revenue and profit impact of a price change for a given item.
     *
     * @param itemId         the menu item ID
     * @param priceChangePct price change percentage (-30 to +50)
     * @return simulation result DTO
     */
    public SimulationResult simulate(Long itemId, double priceChangePct) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        double basePrice     = item.getSellingPrice();
        double unitCost      = item.getCostPrice();
        int    baseDemand    = DEFAULT_BASELINE_DEMAND;
        double elasticity    = DEFAULT_ELASTICITY;

        double newPrice          = basePrice * (1.0 + priceChangePct / 100.0);
        double demandChangePct   = priceChangePct * elasticity;
        int    newDemand         = (int) Math.max(0, Math.round(baseDemand * (1.0 + demandChangePct / 100.0)));

        double baseRevenue   = basePrice   * baseDemand;
        double newRevenue    = newPrice    * newDemand;
        double revenueChange = newRevenue  - baseRevenue;

        double baseProfit    = (basePrice - unitCost) * baseDemand;
        double newProfit     = (newPrice  - unitCost) * newDemand;
        double profitChange  = newProfit  - baseProfit;

        double baseMarginPct = basePrice > 0 ? ((basePrice - unitCost) / basePrice) * 100.0 : 0.0;
        double newMarginPct  = newPrice  > 0 ? ((newPrice  - unitCost) / newPrice)  * 100.0 : 0.0;

        log.debug("Simulation for item '{}': {}% price change → demand {}→{}, revenue ৳{}→৳{}",
                item.getName(), priceChangePct, baseDemand, newDemand,
                String.format("%.0f", baseRevenue), String.format("%.0f", newRevenue));

        return SimulationResult.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .basePrice(basePrice)
                .newPrice(round2(newPrice))
                .priceChangePct(priceChangePct)
                .baseDemandUnits(baseDemand)
                .newDemandUnits(newDemand)
                .baseRevenue(round2(baseRevenue))
                .newRevenue(round2(newRevenue))
                .revenueChange(round2(revenueChange))
                .baseProfit(round2(baseProfit))
                .newProfit(round2(newProfit))
                .profitChange(round2(profitChange))
                .baseMarginPct(round2(baseMarginPct))
                .newMarginPct(round2(newMarginPct))
                .recommendation(buildRecommendation(priceChangePct, revenueChange, profitChange))
                .build();
    }

    /**
     * Returns simulation results for all items at a given price change.
     */
    public List<SimulationResult> simulateAll(double priceChangePct) {
        return itemRepository.findAll().stream()
                .map(item -> simulate(item.getId(), priceChangePct))
                .toList();
    }

    private String buildRecommendation(double changePct, double revDelta, double profitDelta) {
        if (profitDelta > 0 && revDelta > 0) {
            return "Profitable — both revenue and profit increase at this price point.";
        } else if (profitDelta > 0 && revDelta <= 0) {
            return "Higher margin but lower volume — suitable for premium positioning.";
        } else if (profitDelta <= 0 && revDelta > 0) {
            return "Higher volume but tighter margin — suitable for market share strategy.";
        } else {
            return "Not recommended — both revenue and profit decline at this price point.";
        }
    }

    private double round2(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    @Data
    @Builder
    public static class SimulationResult {
        private Long itemId;
        private String itemName;
        private double basePrice;
        private double newPrice;
        private double priceChangePct;
        private int baseDemandUnits;
        private int newDemandUnits;
        private double baseRevenue;
        private double newRevenue;
        private double revenueChange;
        private double baseProfit;
        private double newProfit;
        private double profitChange;
        private double baseMarginPct;
        private double newMarginPct;
        private String recommendation;
    }
}
