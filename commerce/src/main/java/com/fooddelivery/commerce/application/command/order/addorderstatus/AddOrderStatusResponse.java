package com.fooddelivery.commerce.application.command.order.addorderstatus;

import com.fooddelivery.commerce.domain.order.constant.OrderStatus;

public record AddOrderStatusResponse(
        OrderStatus.Status newStatus
) {}
