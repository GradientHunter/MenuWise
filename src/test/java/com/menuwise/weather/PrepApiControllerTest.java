package com.menuwise.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = {"ROLE_ADMIN", "ROLE_CHEF"})
class PrepApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPrepRecommendations_ShouldReturnPopulatedRecommendationsList() throws Exception {
        mockMvc.perform(get("/api/v1/prep/recommendations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(8))))
                .andExpect(jsonPath("$[0].itemId").exists())
                .andExpect(jsonPath("$[0].itemName").exists())
                .andExpect(jsonPath("$[0].baselineQuantity").value(20))
                .andExpect(jsonPath("$[0].recommendedQuantity").isNumber())
                .andExpect(jsonPath("$[0].weatherMultiplier").isNumber())
                .andExpect(jsonPath("$[0].rationale").isString());
    }

    @Test
    void getWeatherForecast_ShouldReturnForecastMetrics() throws Exception {
        mockMvc.perform(get("/api/v1/prep/weather")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperatureCelsius").isNumber())
                .andExpect(jsonPath("$.condition").isString())
                .andExpect(jsonPath("$.humidity").isNumber())
                .andExpect(jsonPath("$.rainProbability").isNumber())
                .andExpect(jsonPath("$.isInclementWeather").isBoolean());
    }
}
