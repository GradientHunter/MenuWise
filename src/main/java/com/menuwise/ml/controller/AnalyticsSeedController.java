package com.menuwise.ml.controller;

import com.menuwise.ml.service.DataIngestionService;
import com.menuwise.ml.service.SyntheticDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsSeedController {

    private final SyntheticDataService syntheticDataService;
    private final DataIngestionService dataIngestionService;

    @PostMapping("/seed-data")
    public ResponseEntity<Map<String, Object>> seedSyntheticData(@RequestParam(defaultValue = "90") int days) {
        String result = syntheticDataService.generateHistoricalData(days);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "days", days,
                "message", result
        ));
    }

    @GetMapping("/velocities")
    public ResponseEntity<Map<String, Map<String, Double>>> getConsumptionVelocities() {
        return ResponseEntity.ok(dataIngestionService.getIngredientConsumptionVelocities());
    }
}
