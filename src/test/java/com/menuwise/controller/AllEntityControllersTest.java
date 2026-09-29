package com.menuwise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.menuwise.domain.category.dto.CategoryRequestDto;
import com.menuwise.domain.inventory.dto.IngredientRequestDto;
import com.menuwise.domain.inventory.dto.InventoryAdjustmentRequestDto;
import com.menuwise.domain.inventory.dto.SupplierRequestDto;
import com.menuwise.domain.inventory.dto.WasteLogRequestDto;
import com.menuwise.domain.menu.dto.ItemRequestDto;
import com.menuwise.order.dto.OrderItemRequestDto;
import com.menuwise.order.dto.OrderRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AllEntityControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void categoryController_ShouldCreateAndFetchCategories() throws Exception {
        CategoryRequestDto request = CategoryRequestDto.builder()
                .name("Special Mocktails")
                .description("Handcrafted seasonal beverages")
                .build();
        
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Special Mocktails"));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))));
    }

    @Test
    void categoryController_ValidationFailure_ShouldReturn400() throws Exception {
        CategoryRequestDto invalidRequest = CategoryRequestDto.builder()
                .name("") // Blank name violates @NotBlank
                .build();

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists());
    }

    @Test
    void itemController_ShouldCreateAndFetchItems() throws Exception {
        ItemRequestDto request = ItemRequestDto.builder()
                .name("Truffle Fries")
                .categoryId(1L)
                .costPrice(2.50)
                .sellingPrice(8.99)
                .prepTimeMin(10)
                .build();

        mockMvc.perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Truffle Fries"));

        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(8))));
    }

    @Test
    void supplierController_ShouldCreateAndFetchSuppliers() throws Exception {
        SupplierRequestDto request = SupplierRequestDto.builder()
                .name("Artisan Bakeries Ltd")
                .contactInfo("orders@artisanbakery.com")
                .leadTimeDays(2)
                .build();

        mockMvc.perform(post("/api/v1/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Artisan Bakeries Ltd"));

        mockMvc.perform(get("/api/v1/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void ingredientController_ShouldCreateAndFetchAlerts() throws Exception {
        IngredientRequestDto request = IngredientRequestDto.builder()
                .name("Avocado")
                .unit("kg")
                .currentStock(5.0)
                .minimumStock(3.0)
                .costPerUnit(6.00)
                .expiryDate(LocalDate.now().plusDays(5))
                .build();

        mockMvc.perform(post("/api/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(get("/api/v1/ingredients/alerts"))
                .andExpect(status().isOk());
    }

    @Test
    void orderController_ShouldCheckoutAndDeductStock() throws Exception {
        OrderRequestDto orderRequest = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .itemId(1L) // Classic Cheeseburger
                        .quantity(2)
                        .build()))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.totalAmount").isNumber());

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void wasteLogController_ShouldRecordWaste() throws Exception {
        WasteLogRequestDto request = WasteLogRequestDto.builder()
                .ingredientId(1L)
                .quantityWasted(1.5)
                .estimatedCost(12.75)
                .reason("Expired over weekend")
                .build();

        mockMvc.perform(post("/api/v1/waste-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void inventoryLogController_ShouldAdjustStock() throws Exception {
        InventoryAdjustmentRequestDto request = InventoryAdjustmentRequestDto.builder()
                .ingredientId(1L)
                .changeAmount(10.0)
                .changeType("RESTOCK")
                .reason("Weekly vendor delivery")
                .build();

        mockMvc.perform(post("/api/v1/inventory-logs/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }
}
