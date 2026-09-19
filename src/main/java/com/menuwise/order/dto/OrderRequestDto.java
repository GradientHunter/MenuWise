package com.menuwise.order.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequestDto> items;
}
