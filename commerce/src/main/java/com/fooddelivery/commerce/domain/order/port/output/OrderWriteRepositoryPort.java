package com.fooddelivery.commerce.domain.order.port.output;

import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;

public interface OrderWriteRepositoryPort {
    OrderId saveNewOrder(OrderAggregate orderAggregate);
    void changeStatus(OrderId orderId, OrderStatus.Status newStatus);
}
