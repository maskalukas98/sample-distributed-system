package com.fooddelivery.commerce.domain.order.entity;

import com.fooddelivery.commerce.domain.model.Entity;
import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import com.fooddelivery.commerce.domain.order.valueobject.OrderStatusId;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public final class OrderStatusEntity extends Entity<OrderStatusId> {
    private OrderId orderId;
    private OrderStatus.Status status;
    private Boolean isActive;
    private LocalDateTime updatedAt;
}
