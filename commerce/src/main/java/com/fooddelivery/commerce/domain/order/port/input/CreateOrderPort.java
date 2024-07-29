package com.fooddelivery.commerce.domain.order.port.input;

import com.fooddelivery.commerce.application.command.order.createorder.CreateOrderCommand;
import com.fooddelivery.commerce.application.command.order.createorder.CreateOrderResponse;

public interface CreateOrderPort {
    CreateOrderResponse create(CreateOrderCommand command);
}
