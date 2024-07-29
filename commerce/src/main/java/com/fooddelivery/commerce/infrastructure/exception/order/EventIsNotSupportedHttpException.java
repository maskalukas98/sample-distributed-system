package com.fooddelivery.commerce.infrastructure.exception.order;

import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EventIsNotSupportedHttpException  {

    private OrderStatus.Status currentState;
    private List<OrderStatus.Event> supportedEvents = new ArrayList<>();
    private String message;
    public EventIsNotSupportedHttpException(
            OrderStatus.Status currentState,
            List<OrderStatus.Event> supportedEvents
    ) {
        this.currentState = currentState;
        this.supportedEvents = supportedEvents;
        this.message = "Not supported event for the current state.";
    }
}
