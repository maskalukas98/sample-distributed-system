package com.fooddelivery.commerce.infrastructure.exception.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderNotExistsHttpException {
    private String message;

    public OrderNotExistsHttpException(long orderId) {
        message = "Order with id: " + orderId + " not exists.";
    }
}
