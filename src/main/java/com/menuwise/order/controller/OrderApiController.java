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

                    return Map.<String, Object>of(
                            "id",          order.getId(),
                            "itemNames",   itemNames,
                            "date",        order.getOrderTimestamp().format(dateFmt),
                            "time",        order.getOrderTimestamp().format(timeFmt),
                            "amount",      order.getTotalAmount(),
                            "status",      order.getStatus().name()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(recent);
    }
}
