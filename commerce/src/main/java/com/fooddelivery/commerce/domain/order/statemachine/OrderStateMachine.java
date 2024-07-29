package com.fooddelivery.commerce.domain.order.statemachine;

import com.fooddelivery.commerce.common.builder.StateMachine;
import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import lombok.Getter;


@Getter
public class OrderStateMachine  {
    private final StateMachine<OrderStatus.Status, OrderStatus.Event> machine = new StateMachine<>();

    public OrderStateMachine() {
        machine
                .initial(OrderStatus.Status.created)
                .setTransition(OrderStatus.Status.created, OrderStatus.Status.delivering, OrderStatus.Event.deliver)
                .setTransition(OrderStatus.Status.delivering, OrderStatus.Status.delivered, OrderStatus.Event.complete)
                .setTransition(OrderStatus.Status.created, OrderStatus.Status.cancelled, OrderStatus.Event.cancel)
                .setTransition(OrderStatus.Status.delivering, OrderStatus.Status.cancelled, OrderStatus.Event.cancel);
    }
}