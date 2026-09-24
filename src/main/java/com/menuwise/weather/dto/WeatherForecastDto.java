package com.menuwise.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing weather forecast metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastDto {

    private Double temperatureCelsius;
    private String condition; // e.g. "Rain", "Clear", "Snow", "Clouds"
    private Integer humidity;
    private Double rainProbability;
    private Boolean isInclementWeather;
}
