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

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Free live WeatherClient powered by Open-Meteo (open-meteo.com).
 * Requires NO API key and provides accurate real-time forecasts globally.
 */
@Component
@ConditionalOnProperty(name = "menuwise.weather.mock-enabled", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class OpenMeteoWeatherClient implements WeatherClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${menuwise.weather.city:Dhaka}")
    private String defaultCity;

    private static final Map<String, double[]> KNOWN_COORDINATES = new ConcurrentHashMap<>(Map.of(
            "dhaka", new double[]{23.8103, 90.4125},
            "chittagong", new double[]{22.3569, 91.7832},
            "sylhet", new double[]{24.8949, 91.8687},
            "new york", new double[]{40.7128, -74.0060},
            "london", new double[]{51.5074, -0.1278}
    ));

    @Override
    public WeatherForecastDto getFiveDayForecast(String city) {
        String targetCity = (city != null && !city.trim().isEmpty()) ? city.trim() : defaultCity;
        log.info("Querying live Open-Meteo weather API for city: {}", targetCity);

        try {
            double[] coords = resolveCoordinates(targetCity);
            if (coords == null) {
                log.warn("Could not geocode city '{}'. Using Dhaka fallback coordinates.", targetCity);
                coords = new double[]{23.8103, 90.4125};
            }

            String forecastUrl = String.format(Locale.US,
                    "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,weather_code,precipitation&daily=precipitation_probability_max&timezone=auto",
                    coords[0], coords[1]);

            String response = restTemplate.getForObject(forecastUrl, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode current = root.path("current");

            double temp = current.path("temperature_2m").asDouble(26.0);
            int humidity = current.path("relative_humidity_2m").asInt(65);
            int weatherCode = current.path("weather_code").asInt(0);

            // Read precipitation probability from daily forecast if present
            double rainProb = 0.10;
            JsonNode dailyProb = root.path("daily").path("precipitation_probability_max");
            if (dailyProb.isArray() && !dailyProb.isEmpty()) {
                rainProb = dailyProb.get(0).asDouble(10.0) / 100.0;
            }

            String condition = mapWmoCodeToCondition(weatherCode);
            boolean isInclement = isConditionInclement(weatherCode, condition);

            if (isInclement && rainProb < 0.60) {
                rainProb = 0.75;
            }

            return WeatherForecastDto.builder()
                    .temperatureCelsius(Math.round(temp * 10.0) / 10.0)
                    .condition(condition)
                    .humidity(humidity)
                    .rainProbability(Math.round(rainProb * 100.0) / 100.0)
                    .isInclementWeather(isInclement)
                    .city(targetCity)
                    .build();

        } catch (Exception e) {
            log.warn("Failed to query live Open-Meteo weather for '{}': {}. Falling back to default baseline.", targetCity, e.getMessage());
            return WeatherForecastDto.builder()
                    .temperatureCelsius(26.0)
                    .condition("Clouds")
                    .humidity(65)
                    .rainProbability(0.20)
                    .isInclementWeather(false)
                    .city(targetCity)
                    .build();
        }
    }

    private double[] resolveCoordinates(String city) {
        String key = city.trim().toLowerCase(Locale.ROOT);
        if (KNOWN_COORDINATES.containsKey(key)) {
            return KNOWN_COORDINATES.get(key);
        }

        try {
            String geoUrl = String.format("https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1&language=en&format=json", city.trim());
            String response = restTemplate.getForObject(geoUrl, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.path("results");
            if (results.isArray() && !results.isEmpty()) {
                JsonNode first = results.get(0);
                double lat = first.path("latitude").asDouble();
                double lon = first.path("longitude").asDouble();
                double[] coords = new double[]{lat, lon};
                KNOWN_COORDINATES.put(key, coords);
                return coords;
            }
        } catch (Exception e) {
            log.debug("Geocoding lookup failed for '{}': {}", city, e.getMessage());
        }
        return null;
    }

    private String mapWmoCodeToCondition(int code) {
        if (code == 0) return "Clear";
        if (code >= 1 && code <= 3) return "Clouds";
        if (code == 45 || code == 48) return "Fog";
        if (code >= 51 && code <= 57) return "Drizzle";
        if (code >= 61 && code <= 67) return "Rain";
        if (code >= 71 && code <= 77) return "Snow";
        if (code >= 80 && code <= 82) return "Rain";
        if (code >= 85 && code <= 86) return "Snow";
        if (code >= 95 && code <= 99) return "Thunderstorm";
        return "Clouds";
    }

    private boolean isConditionInclement(int code, String condition) {
        return code >= 51 // drizzle, rain, snow, showers, thunderstorms
                || "Rain".equalsIgnoreCase(condition)
                || "Snow".equalsIgnoreCase(condition)
                || "Thunderstorm".equalsIgnoreCase(condition)
                || "Drizzle".equalsIgnoreCase(condition);
    }
}
