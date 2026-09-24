package com.menuwise.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testDashboardRoute() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/dashboard"))
                .andExpect(model().attribute("activeRoute", "dashboard"));
    }

    @Test
    void testDashboardExplicitPathRoute() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/dashboard"))
                .andExpect(model().attribute("activeRoute", "dashboard"));
    }

    @Test
    void testPosRoute() throws Exception {
        mockMvc.perform(get("/pos"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/pos"))
                .andExpect(model().attribute("activeRoute", "pos"));
    }

    @Test
    void testInventoryRoute() throws Exception {
        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/inventory"))
                .andExpect(model().attribute("activeRoute", "inventory"));
    }

    @Test
    void testAnalyticsRoute() throws Exception {
        mockMvc.perform(get("/analytics"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/analytics"))
                .andExpect(model().attribute("activeRoute", "analytics"));
    }

    @Test
    void testSimulatorRoute() throws Exception {
        mockMvc.perform(get("/simulator"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/simulator"))
                .andExpect(model().attribute("activeRoute", "simulator"));
    }
}
