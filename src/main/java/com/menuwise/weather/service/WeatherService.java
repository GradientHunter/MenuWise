package com.menuwise.weather.service;

import com.menuwise.weather.client.WeatherClient;
import com.menuwise.weather.dto.WeatherForecastDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service orchestrating weather signal retrieval with Spring Cache integration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final WeatherClient weatherClient;

    @Value("${menuwise.weather.city:Dhaka}")
    private String defaultCity;

    /**
     * Retrieves weather forecast for a target city with caching enabled.
     *
     * @param city target city name
     * @return weather forecast DTO
     */
    @Cacheable(value = "weatherForecast", key = "#city != null ? #city : 'default'")
    public WeatherForecastDto getCurrentForecast(String city) {
        String targetCity = (city != null && !city.trim().isEmpty()) ? city.trim() : defaultCity;
        log.info("Retrieving weather forecast for '{}' (cache miss)", targetCity);
        return weatherClient.getFiveDayForecast(targetCity);
    }

    /**
     * Retrieves weather forecast for default configured restaurant city.
     *
     * @return weather forecast DTO
     */
    public WeatherForecastDto getDefaultForecast() {
        return getCurrentForecast(defaultCity);
    }

    /**
     * Evicts cached weather forecasts and prep recommendations to force a live refresh.
     */
    @CacheEvict(value = {"weatherForecast", "dailyPrepRecommendations"}, allEntries = true)
    public void evictWeatherCache() {
        log.info("Evicted weatherForecast and dailyPrepRecommendations caches");
    }
}
