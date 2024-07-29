package com.fooddelivery.commerce.domain.order.exception;

import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderIsAlreadyInFinalStateException extends RuntimeException {
    private OrderStatus.Status finalState;
    public OrderIsAlreadyInFinalStateException(OrderId orderId, OrderStatus.Status finalState) {
        super("Order " + orderId.getValue() + " is already in final state: " + finalState + ".");
        this.finalState = finalState;
    }
}
