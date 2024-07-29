package com.fooddelivery.commerce.domain.order.port.input;

import com.fooddelivery.commerce.application.command.order.getorder.GetOrderCommand;
import com.fooddelivery.commerce.application.command.order.getorder.GetOrderResponse;

public interface GetOrderPort {
    GetOrderResponse getById(GetOrderCommand command);
}
