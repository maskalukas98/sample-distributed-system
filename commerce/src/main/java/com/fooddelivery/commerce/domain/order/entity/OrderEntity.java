package com.fooddelivery.commerce.domain.order.entity;

import com.fooddelivery.commerce.domain.model.Entity;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public final class OrderEntity extends Entity<OrderId> {
    private int customerId;
    private ProductId productId;
    private String address;
    private LocalDateTime createdAt;
}
