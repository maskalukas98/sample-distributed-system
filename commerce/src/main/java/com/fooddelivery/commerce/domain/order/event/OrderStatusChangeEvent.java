package com.fooddelivery.commerce.domain.order.event;

public record OrderStatusChangeEvent(
        long orderId,
        String status
) {}
