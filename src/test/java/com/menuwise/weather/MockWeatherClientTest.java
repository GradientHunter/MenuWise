package com.menuwise.weather;

import com.menuwise.ml.dto.DailyConsumptionRecord;
import com.menuwise.ml.dto.SpoilageForecastDto;
import com.menuwise.weather.client.MockWeatherClient;
import com.menuwise.weather.client.WeatherClient;
import com.menuwise.weather.dto.PrepRecommendationDto;
import com.menuwise.weather.dto.WeatherForecastDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MockWeatherClientTest {

    private WeatherClient weatherClient;

    @BeforeEach
    void setUp() {
        weatherClient = new MockWeatherClient();
    }

    @Test
    void getFiveDayForecast_ShouldReturnDeterministicMockData() {
        WeatherForecastDto forecast = weatherClient.getFiveDayForecast("New York");

        assertThat(forecast).isNotNull();
        assertThat(forecast.getTemperatureCelsius()).isEqualTo(8.0);
        assertThat(forecast.getCondition()).isEqualTo("Rain");
        assertThat(forecast.getHumidity()).isEqualTo(82);
        assertThat(forecast.getRainProbability()).isEqualTo(0.80);
        assertThat(forecast.getIsInclementWeather()).isTrue();
    }

    @Test
    void prepRecommendationDto_ShouldStoreAndRetrieveValues() {
        PrepRecommendationDto dto = PrepRecommendationDto.builder()
                .itemId(1L)
                .itemName("Hot Tomato Soup")
                .baselineQuantity(20)
                .recommendedQuantity(26)
                .weatherMultiplier(1.3)
                .rationale("Rainy weather expected to boost demand for warm comfort foods.")
                .build();

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getItemName()).isEqualTo("Hot Tomato Soup");
        assertThat(dto.getBaselineQuantity()).isEqualTo(20);
        assertThat(dto.getRecommendedQuantity()).isEqualTo(26);
        assertThat(dto.getWeatherMultiplier()).isEqualTo(1.3);
        assertThat(dto.getRationale()).contains("Rainy weather");
    }

    @Test
    void dailyConsumptionRecord_ShouldStoreAndRetrieveValues() {
        DailyConsumptionRecord record = DailyConsumptionRecord.builder()
                .date(LocalDate.of(2026, 9, 24))
                .ingredientId(101L)
                .quantityConsumed(15.5)
                .weatherCondition("Rain")
                .isWeekend(false)
                .rolling7DayAvg(14.2)
                .build();

        assertThat(record.getDate()).isEqualTo(LocalDate.of(2026, 9, 24));
        assertThat(record.getIngredientId()).isEqualTo(101L);
        assertThat(record.getQuantityConsumed()).isEqualTo(15.5);
        assertThat(record.getWeatherCondition()).isEqualTo("Rain");
        assertThat(record.getIsWeekend()).isFalse();
        assertThat(record.getRolling7DayAvg()).isEqualTo(14.2);
    }

    @Test
    void spoilageForecastDto_ShouldStoreAndRetrieveValues() {
        SpoilageForecastDto dto = SpoilageForecastDto.builder()
                .ingredientId(101L)
                .ingredientName("Fresh Cream")
                .currentStock(5.0)
                .predictedWasteQty(2.2)
                .spoilageRiskPercentage(44.0)
                .riskTier("MEDIUM")
                .build();

        assertThat(dto.getIngredientId()).isEqualTo(101L);
        assertThat(dto.getIngredientName()).isEqualTo("Fresh Cream");
        assertThat(dto.getCurrentStock()).isEqualTo(5.0);
        assertThat(dto.getPredictedWasteQty()).isEqualTo(2.2);
        assertThat(dto.getSpoilageRiskPercentage()).isEqualTo(44.0);
        assertThat(dto.getRiskTier()).isEqualTo("MEDIUM");
    }
}
