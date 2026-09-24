package com.menuwise.weather.client;

import com.menuwise.weather.dto.WeatherForecastDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of WeatherClient providing deterministic weather signals
 * for offline and local testing environments.
 */
@Component
@ConditionalOnProperty(name = "menuwise.weather.mock-enabled", havingValue = "true", matchIfMissing = true)
public class MockWeatherClient implements WeatherClient {

    @Override
    public WeatherForecastDto getFiveDayForecast(String city) {
        return WeatherForecastDto.builder()
                .temperatureCelsius(8.0)
                .condition("Rain")
                .humidity(82)
                .rainProbability(0.80)
                .isInclementWeather(true)
                .build();
    }
}
