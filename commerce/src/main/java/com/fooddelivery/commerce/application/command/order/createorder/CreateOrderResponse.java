package com.fooddelivery.commerce.application.command.order.createorder;

import com.fooddelivery.commerce.domain.order.constant.OrderStatus;

public record CreateOrderResponse (
        long orderId,
        OrderStatus.Status status
){}
