package com.fooddelivery.commerce.application.command.order.getorder;


import java.time.LocalDateTime;
import java.util.List;

public record GetOrderResponse(
        long id,
        int customerId,
        int productId,
        String address,
        LocalDateTime createdAt,
        List<GetOrderStatusResponse> statuses
) { }