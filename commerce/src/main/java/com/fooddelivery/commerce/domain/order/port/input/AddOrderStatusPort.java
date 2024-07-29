package com.fooddelivery.commerce.domain.order.port.input;

import com.fooddelivery.commerce.application.command.order.addorderstatus.AddOrderStatusCommand;
import com.fooddelivery.commerce.application.command.order.addorderstatus.AddOrderStatusResponse;

public interface AddOrderStatusPort {
    AddOrderStatusResponse addStatus(AddOrderStatusCommand command);
}
