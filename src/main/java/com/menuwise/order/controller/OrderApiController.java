package com.menuwise.order.controller;

import com.menuwise.domain.order.Order;
import com.menuwise.order.dto.OrderRequestDto;
import com.menuwise.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> checkout(@Valid @RequestBody OrderRequestDto request) {
        Order order = orderService.checkoutOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Returns the 10 most recent orders as lightweight summary objects for the dashboard.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Map<String, Object>>> getRecentOrders() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");

        List<Map<String, Object>> recent = orderService.getAllOrders().stream()
                .sorted((a, b) -> b.getOrderTimestamp().compareTo(a.getOrderTimestamp()))
                .limit(10)
                .map(order -> {
                    String itemNames = order.getItems().stream()
                            .map(oi -> oi.getItem() != null ? oi.getItem().getName() : "Item")
                            .collect(Collectors.joining(", "));
                    if (itemNames.isBlank()) itemNames = "Order #" + order.getId();

                    List<Map<String, Object>> itemsList = order.getItems().stream()
                            .map(oi -> {
                                Map<String, Object> itemMap = new HashMap<>();
                                itemMap.put("id", oi.getId() != null ? oi.getId() : 0L);
                                itemMap.put("name", oi.getItem() != null ? oi.getItem().getName() : "Item");
                                itemMap.put("quantity", oi.getQuantity() != null ? oi.getQuantity() : 1);
                                itemMap.put("unitPrice", oi.getUnitPrice() != null ? oi.getUnitPrice() : 0.0);
                                itemMap.put("totalPrice", (oi.getQuantity() != null && oi.getUnitPrice() != null)
                                        ? oi.getQuantity() * oi.getUnitPrice() : 0.0);
                                return itemMap;
                            })
                            .collect(Collectors.toList());

                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("id", order.getId());
                    orderMap.put("itemNames", itemNames);
                    orderMap.put("date", order.getOrderTimestamp().format(dateFmt));
                    orderMap.put("time", order.getOrderTimestamp().format(timeFmt));
                    orderMap.put("amount", order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);
                    orderMap.put("status", order.getStatus() != null ? order.getStatus().name() : "COMPLETED");
                    orderMap.put("items", itemsList);

                    return orderMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(recent);
    }
}
