package com.fooddelivery.commerce.application.command.order.getorder;

import com.fooddelivery.commerce.domain.order.constant.OrderStatus;

import java.time.LocalDateTime;

public record GetOrderStatusResponse(
        long id,
        OrderStatus.Status status,
        boolean isActive,
        LocalDateTime updatedAt
){}
