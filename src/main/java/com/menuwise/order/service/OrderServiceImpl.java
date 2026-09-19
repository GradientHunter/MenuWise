package com.menuwise.order.service;

import com.menuwise.common.exception.InsufficientStockException;
import com.menuwise.common.exception.ResourceNotFoundException;
import com.menuwise.domain.inventory.Ingredient;
import com.menuwise.domain.inventory.InventoryLog;
import com.menuwise.domain.menu.Item;
import com.menuwise.domain.menu.ItemIngredient;
import com.menuwise.domain.order.Order;
import com.menuwise.domain.order.OrderItem;
import com.menuwise.domain.order.OrderStatus;
import com.menuwise.order.dto.OrderItemRequestDto;
import com.menuwise.order.dto.OrderRequestDto;
import com.menuwise.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final IngredientRepository ingredientRepository;
    private final ItemIngredientRepository itemIngredientRepository;
    private final InventoryLogRepository inventoryLogRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order checkoutOrder(OrderRequestDto request) {
        log.info("Processing checkout for order with {} line items...", request.getItems().size());

        // Step 1: Pre-calculate total ingredient quantities required across the entire order
        Map<Long, Double> requiredQuantitiesByIngredient = new HashMap<>();
        Map<Long, Item> itemCache = new HashMap<>();

        for (OrderItemRequestDto itemReq : request.getItems()) {
            Item item = itemRepository.findById(itemReq.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemReq.getItemId()));
            itemCache.put(item.getId(), item);

            List<ItemIngredient> boms = itemIngredientRepository.findByItemId(item.getId());
            for (ItemIngredient bom : boms) {
                Long ingredientId = bom.getIngredient().getId();
                double needed = bom.getQuantityRequired() * itemReq.getQuantity();
                requiredQuantitiesByIngredient.merge(ingredientId, needed, Double::sum);
            }
        }

        // Step 2: Validate available stock and fail-fast with automatic rollback
        for (Map.Entry<Long, Double> entry : requiredQuantitiesByIngredient.entrySet()) {
            Long ingredientId = entry.getKey();
            Double totalNeeded = entry.getValue();

            Ingredient ingredient = ingredientRepository.findById(ingredientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + ingredientId));

            if (ingredient.getCurrentStock() < totalNeeded) {
                log.warn("Insufficient stock for ingredient: {}. Required: {}, Available: {}",
                        ingredient.getName(), totalNeeded, ingredient.getCurrentStock());
                throw new InsufficientStockException(String.format(
                        "Insufficient stock for ingredient '%s'. Required: %.2f %s, Available: %.2f %s",
                        ingredient.getName(), totalNeeded, ingredient.getUnit(), ingredient.getCurrentStock(), ingredient.getUnit()
                ));
            }
        }

        // Step 3: Deduct stock and write audit logs
        for (Map.Entry<Long, Double> entry : requiredQuantitiesByIngredient.entrySet()) {
            Long ingredientId = entry.getKey();
            Double totalNeeded = entry.getValue();

            Ingredient ingredient = ingredientRepository.findById(ingredientId).get();
            ingredient.setCurrentStock(Math.round((ingredient.getCurrentStock() - totalNeeded) * 100.0) / 100.0);
            ingredientRepository.save(ingredient);

            InventoryLog logEntry = InventoryLog.builder()
                    .ingredient(ingredient)
                    .changeAmount(-totalNeeded)
                    .changeType("ORDER_DEDUCTION")
                    .timestamp(LocalDateTime.now())
                    .reason("Automated BOM deduction for order")
                    .build();
            inventoryLogRepository.save(logEntry);
        }

        // Step 4: Persist Order & OrderItems
        Order order = Order.builder()
                .orderTimestamp(LocalDateTime.now())
                .totalAmount(0.0)
                .status(OrderStatus.COMPLETED)
                .build();
        order = orderRepository.save(order);

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDto itemReq : request.getItems()) {
            Item item = itemCache.get(itemReq.getItemId());
            double lineTotal = item.getSellingPrice() * itemReq.getQuantity();
            totalAmount += lineTotal;

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .item(item)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(item.getSellingPrice())
                    .build();
            orderItems.add(orderItem);
        }

        order.setTotalAmount(Math.round(totalAmount * 100.0) / 100.0);
        order.setItems(orderItems);
        orderItemRepository.saveAll(orderItems);

        log.info("Order #{} placed successfully. Total: ${}", order.getId(), order.getTotalAmount());
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
