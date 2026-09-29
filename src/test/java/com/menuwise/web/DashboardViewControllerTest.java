package com.menuwise.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = {"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_CASHIER", "ROLE_CHEF"})
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

    @Test
    void testWeatherRoute() throws Exception {
        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/weather"))
                .andExpect(model().attribute("activeRoute", "weather"));
    }

    @Test
    void testRescueRoute() throws Exception {
        mockMvc.perform(get("/rescue"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/rescue"))
                .andExpect(model().attribute("activeRoute", "rescue"));
    }

    @Test
    void testAdminUsersRoute() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/admin-users"))
                .andExpect(model().attribute("activeRoute", "admin-users"));
    }
}
