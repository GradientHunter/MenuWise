package com.menuwise.repository;

import com.menuwise.domain.order.Order;
import com.menuwise.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByOrderTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderTimestamp >= :since")
    Long countOrdersSince(@Param("since") LocalDateTime since);
}
