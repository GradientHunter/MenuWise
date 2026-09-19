package com.menuwise.ml.service;

import com.menuwise.domain.inventory.Ingredient;
import com.menuwise.domain.inventory.WasteLog;
import com.menuwise.domain.menu.Item;
import com.menuwise.domain.order.Order;
import com.menuwise.domain.order.OrderItem;
import com.menuwise.domain.order.OrderStatus;
import com.menuwise.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyntheticDataService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final WasteLogRepository wasteLogRepository;
    private final ItemRepository itemRepository;
    private final IngredientRepository ingredientRepository;

    private final Random random = new Random(42); // Fixed seed for reproducible generation

    @Transactional
    public String generateHistoricalData(int days) {
        log.info("Generating {} days of synthetic sales and spoilage data...", days);

        List<Item> items = itemRepository.findAll();
        List<Ingredient> ingredients = ingredientRepository.findAll();

        if (items.isEmpty() || ingredients.isEmpty()) {
            return "Cannot generate synthetic data: menu items or ingredients not seeded.";
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        int totalOrdersCreated = 0;
        int totalWasteLogsCreated = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Generate 15-35 orders per day with peak variations
            int ordersToday = 15 + random.nextInt(20);
            for (int i = 0; i < ordersToday; i++) {
                int hour = 11 + random.nextInt(11); // 11 AM to 10 PM
                int minute = random.nextInt(60);
                LocalDateTime orderTime = date.atTime(hour, minute);

                // 1 to 3 items per order
                int orderItemCount = 1 + random.nextInt(3);
                double orderTotal = 0.0;
                List<OrderItem> orderItems = new ArrayList<>();

                Order order = Order.builder()
                        .orderTimestamp(orderTime)
                        .totalAmount(0.0)
                        .status(OrderStatus.COMPLETED)
                        .build();
                order = orderRepository.save(order);

                for (int j = 0; j < orderItemCount; j++) {
                    Item selectedItem = items.get(random.nextInt(items.size()));
                    int quantity = 1 + random.nextInt(2);
                    double unitPrice = selectedItem.getSellingPrice();
                    orderTotal += unitPrice * quantity;

                    OrderItem orderItem = OrderItem.builder()
                            .order(order)
                            .item(selectedItem)
                            .quantity(quantity)
                            .unitPrice(unitPrice)
                            .build();
                    orderItems.add(orderItem);
                }

                order.setTotalAmount(Math.round(orderTotal * 100.0) / 100.0);
                order.setItems(orderItems);
                orderItemRepository.saveAll(orderItems);
                totalOrdersCreated++;
            }

            // Generate 0-3 waste logs per day (e.g., spoilage, prep error)
            int wasteCount = random.nextInt(4);
            for (int w = 0; w < wasteCount; w++) {
                Ingredient spoiledIngredient = ingredients.get(random.nextInt(ingredients.size()));
                double qty = 0.2 + (random.nextDouble() * 2.0); // 0.2 to 2.2 units
                qty = Math.round(qty * 10.0) / 10.0;
                double cost = Math.round((qty * spoiledIngredient.getCostPerUnit()) * 100.0) / 100.0;

                String[] reasons = {"EXPIRED", "SPOILED", "PREP_TRIM", "DAMAGED"};
                WasteLog wasteLog = WasteLog.builder()
                        .ingredient(spoiledIngredient)
                        .quantityWasted(qty)
                        .estimatedCost(cost)
                        .logDate(date)
                        .reason(reasons[random.nextInt(reasons.length)])
                        .build();
                wasteLogRepository.save(wasteLog);
                totalWasteLogsCreated++;
            }
        }

        log.info("Synthetic data generation finished. Created {} orders and {} waste logs.",
                totalOrdersCreated, totalWasteLogsCreated);

        return String.format("Successfully seeded %d days of data: %d orders and %d waste logs created.",
                days, totalOrdersCreated, totalWasteLogsCreated);
    }
}
