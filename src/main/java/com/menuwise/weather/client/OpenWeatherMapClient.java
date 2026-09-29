package com.menuwise.weather.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.menuwise.weather.dto.WeatherForecastDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Live OpenWeatherMap API client for fetching real-time weather forecasts.
 * Active when menuwise.weather.mock-enabled is set to false.
 */
@Component
@ConditionalOnProperty(name = "menuwise.weather.provider", havingValue = "openweathermap")
@RequiredArgsConstructor
@Slf4j
public class OpenWeatherMapClient implements WeatherClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${menuwise.weather.api-key:demo_key}")
    private String apiKey;

    @Value("${menuwise.weather.base-url:https://api.openweathermap.org/data/2.5}")
    private String baseUrl;

    @Override
    public WeatherForecastDto getFiveDayForecast(String city) {
        String url = String.format("%s/weather?q=%s&appid=%s&units=metric", baseUrl, city, apiKey);
        log.info("Querying live OpenWeatherMap API for city: {}", city);

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            double temp = root.path("main").path("temp").asDouble(20.0);
            int humidity = root.path("main").path("humidity").asInt(50);
            String condition = "Clear";
            if (root.has("weather") && root.path("weather").isArray() && !root.path("weather").isEmpty()) {
                condition = root.path("weather").get(0).path("main").asText("Clear");
            }

            boolean isInclement = condition.equalsIgnoreCase("Rain")
                    || condition.equalsIgnoreCase("Snow")
                    || condition.equalsIgnoreCase("Thunderstorm")
                    || condition.equalsIgnoreCase("Drizzle");

            double rainProb = isInclement ? 0.85 : 0.10;

            return WeatherForecastDto.builder()
                    .temperatureCelsius(temp)
                    .condition(condition)
                    .humidity(humidity)
                    .rainProbability(rainProb)
                    .isInclementWeather(isInclement)
                    .build();

        } catch (Exception e) {
            log.warn("Failed to retrieve live weather from OpenWeatherMap for '{}': {}. Falling back to default baseline.", city, e.getMessage());
            return WeatherForecastDto.builder()
                    .temperatureCelsius(8.0)
                    .condition("Rain")
                    .humidity(82)
                    .rainProbability(0.80)
                    .isInclementWeather(true)
                    .build();
        }
    }
}
