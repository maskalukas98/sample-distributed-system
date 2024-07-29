package com.fooddelivery.commerce.application.exception.order;

import com.fooddelivery.commerce.domain.order.valueobject.OrderId;

public class StatusAlreadyExistsException extends RuntimeException {
    public StatusAlreadyExistsException(OrderId orderId, String status) {
        super("Status " + status + " for order with id: " + orderId.getValue() + " already exists.");
    }
}
