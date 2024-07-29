package com.fooddelivery.commerce.application.exception.order;

import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import lombok.Getter;

@Getter
public class OrderNotExistsException extends RuntimeException {
    private OrderId orderId;
    public OrderNotExistsException(OrderId orderId) {
        super("Order with id: " + orderId + " not exists.");
        this.orderId = orderId;
    }
}
