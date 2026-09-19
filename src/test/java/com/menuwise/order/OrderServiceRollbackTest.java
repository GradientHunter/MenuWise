package com.menuwise.order;

import com.menuwise.common.exception.InsufficientStockException;
import com.menuwise.domain.inventory.Ingredient;
import com.menuwise.domain.menu.Item;
import com.menuwise.domain.menu.ItemIngredient;
import com.menuwise.domain.menu.ItemIngredientId;
import com.menuwise.order.dto.OrderItemRequestDto;
import com.menuwise.order.dto.OrderRequestDto;
import com.menuwise.order.service.OrderService;
import com.menuwise.repository.IngredientRepository;
import com.menuwise.repository.ItemIngredientRepository;
import com.menuwise.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderServiceRollbackTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ItemIngredientRepository itemIngredientRepository;

    private Item testDish;
    private Ingredient testIngredient;

    @BeforeEach
    void setUp() {
        testIngredient = ingredientRepository.save(Ingredient.builder()
                .name("Truffle Oil")
                .unit("L")
                .currentStock(1.0)
                .minimumStock(0.5)
                .costPerUnit(50.0)
                .expiryDate(LocalDate.now().plusDays(10))
                .build());

        testDish = itemRepository.save(Item.builder()
                .name("Truffle Risotto")
                .costPrice(10.0)
                .sellingPrice(28.0)
                .prepTimeMin(20)
                .build());

        ItemIngredientId id = new ItemIngredientId(testDish.getId(), testIngredient.getId());
        itemIngredientRepository.save(ItemIngredient.builder()
                .id(id)
                .item(testDish)
                .ingredient(testIngredient)
                .quantityRequired(0.2) // 0.2 L per dish
                .build());
    }

    @Test
    @DisplayName("Should successfully checkout order and deduct stock when stock is sufficient")
    void testCheckoutSuccess() {
        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder().itemId(testDish.getId()).quantity(2).build()))
                .build();

        assertDoesNotThrow(() -> orderService.checkoutOrder(request));

        Ingredient updatedIngredient = ingredientRepository.findById(testIngredient.getId()).orElseThrow();
        assertEquals(0.6, updatedIngredient.getCurrentStock(), 0.01);
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when stock is deficient")
    void testCheckoutRollbackOnInsufficientStock() {
        // Request 10 dishes = 2.0 L required, but only 1.0 L is in stock
        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder().itemId(testDish.getId()).quantity(10).build()))
                .build();

        assertThrows(InsufficientStockException.class, () -> orderService.checkoutOrder(request));

        // Verify stock remains untouched (rollback verified)
        Ingredient updatedIngredient = ingredientRepository.findById(testIngredient.getId()).orElseThrow();
        assertEquals(1.0, updatedIngredient.getCurrentStock(), 0.01);
    }
}
