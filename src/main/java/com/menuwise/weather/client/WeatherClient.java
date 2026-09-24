package com.menuwise.weather.client;

import com.menuwise.weather.dto.WeatherForecastDto;

/**
 * Interface for retrieving weather forecast signals.
 */
public interface WeatherClient {

    /**
     * Retrieve forecast metrics for a given city.
     *
     * @param city target city name
     * @return forecast data transfer object
     */
    WeatherForecastDto getFiveDayForecast(String city);
}
