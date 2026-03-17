package com.enzo.restaurant_api.dto;

import com.enzo.restaurant_api.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private Instant createdAt;
    private BigDecimal totalAmount;
    private RestaurantResponse restaurant;
    private List<OrderItemResponse> items;
}
