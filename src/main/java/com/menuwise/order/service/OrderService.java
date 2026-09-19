package com.menuwise.order.service;

import com.menuwise.domain.order.Order;
import com.menuwise.order.dto.OrderRequestDto;

import java.util.List;

public interface OrderService {
    Order checkoutOrder(OrderRequestDto request);
    Order getOrderById(Long orderId);
    List<Order> getAllOrders();
}
