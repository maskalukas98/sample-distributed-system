package com.fooddelivery.commerce.domain.order.port.output;

import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;

public interface OrderReadRepositoryPort {
    OrderAggregate findOrderById(OrderId orderId);
}
