package com.menuwise.order.controller;

import com.menuwise.domain.order.Order;
import com.menuwise.order.dto.OrderRequestDto;
import com.menuwise.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
