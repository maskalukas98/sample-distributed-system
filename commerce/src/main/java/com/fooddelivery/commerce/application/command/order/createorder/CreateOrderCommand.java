package com.fooddelivery.commerce.application.command.order.createorder;

public record CreateOrderCommand (
        int productId,
        int customerId,
        String address
){}
