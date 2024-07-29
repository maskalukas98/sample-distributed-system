package com.fooddelivery.commerce.unit.domain.order;

import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import com.fooddelivery.commerce.domain.order.entity.OrderEntity;
import com.fooddelivery.commerce.domain.order.entity.OrderStatusEntity;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class OrderAggregateTest {
    private OrderEntity orderEntity;

    @BeforeEach
    void setup() {
        orderEntity = new OrderEntity();
        orderEntity.setId(new OrderId(100L));
    }
    @Test
    public void testCreateNew_shouldCreateNewInstance() {
        // execute
        final OrderAggregate orderAggregate = OrderAggregate.createNew(orderEntity);

        // verify
        final OrderStatusEntity status = orderAggregate.getStatuses().get(0);

        Assertions.assertEquals(orderAggregate.getStatuses().size(), 1);
        Assertions.assertEquals(status.getStatus().toString(), "created");
        Assertions.assertEquals(status.getIsActive(), true);
        Assertions.assertEquals(orderAggregate.getId().getValue(), 100L);
    }

    @Test
    public void testGetCurrentStatus_shouldGetCurrentStatusCreated() {
        final OrderAggregate orderAggregate = OrderAggregate.createNew(orderEntity);

        // execute
        OrderStatusEntity orderStatusEntity = orderAggregate.getCurrentStatus();

        // verify
        Assertions.assertEquals(orderStatusEntity.getStatus(), OrderStatus.Status.created);
    }

    @Test
    public void testGetCurrentStatus_shouldGetCurrentStatusDelivering() {
        final OrderAggregate orderAggregate = OrderAggregate.createNew(orderEntity);
        orderAggregate.addStatus(OrderStatus.Event.deliver);

        // execute
        OrderStatusEntity orderStatusEntity = orderAggregate.getCurrentStatus();

        // verify
        Assertions.assertEquals(orderStatusEntity.getStatus(), OrderStatus.Status.delivering);
    }

    @Test
    public void testDddStatus_shouldAddNewStatus() {
        final OrderAggregate orderAggregate = OrderAggregate.createNew(orderEntity);

        // execute
        OrderStatusEntity newStatus = orderAggregate.addStatus(OrderStatus.Event.deliver);

        // verify
        Assertions.assertEquals(orderAggregate.getStatuses().size(), 2);
        Assertions.assertEquals(newStatus.getStatus(), OrderStatus.Status.delivering);
    }
}
