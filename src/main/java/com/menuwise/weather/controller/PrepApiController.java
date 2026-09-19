package com.menuwise.weather.controller;

import com.menuwise.weather.dto.PrepRecommendationDto;
import com.menuwise.weather.service.WeatherAdaptivePrepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prep")
@RequiredArgsConstructor
public class PrepApiController {

    private final WeatherAdaptivePrepService prepService;

    @GetMapping("/recommendations")
    public ResponseEntity<List<PrepRecommendationDto>> getPrepRecommendations() {
        return ResponseEntity.ok(prepService.getDailyPrepRecommendations());
    }
}
