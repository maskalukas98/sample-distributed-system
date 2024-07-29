package com.fooddelivery.commerce.domain.order.exception;

import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderStateDoesNotSupportEventException extends RuntimeException {
    private OrderId orderId;
    private OrderStatus.Status currentState;
    private List<OrderStatus.Event> supportedEvents = new ArrayList<>();

    public OrderStateDoesNotSupportEventException(
            OrderId orderId,
            OrderStatus.Status currentState,
            List<OrderStatus.Event> supportedEvents
    ) {
        super("Order " + orderId.getValue() + "with status " + currentState.toString() + " not support provided event.");
        this.supportedEvents = supportedEvents;
        this.currentState = currentState;
    }
}
