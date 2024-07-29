package com.fooddelivery.commerce.domain.order;

import com.fooddelivery.commerce.common.builder.StateMachine;
import com.fooddelivery.commerce.domain.model.AggregateRoot;
import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import com.fooddelivery.commerce.domain.order.entity.OrderEntity;
import com.fooddelivery.commerce.domain.order.entity.OrderStatusEntity;
import com.fooddelivery.commerce.domain.order.exception.OrderIsAlreadyInFinalStateException;
import com.fooddelivery.commerce.domain.order.exception.OrderStateDoesNotSupportEventException;
import com.fooddelivery.commerce.domain.order.statemachine.OrderStateMachine;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public final class OrderAggregate extends AggregateRoot<OrderId> {
    @Getter
    @Setter
    private OrderEntity order;

    @Getter
    @Setter
    private List<OrderStatusEntity> statuses = new ArrayList<>();
    private final StateMachine<OrderStatus.Status, OrderStatus.Event> stateMachine;

    OrderAggregate() {
        stateMachine = new OrderStateMachine().getMachine();
    }

    public static OrderAggregate createNew(OrderEntity order) {
        final OrderAggregate orderAggregate = new OrderAggregate();
        orderAggregate.setId(order.getId());
        orderAggregate.setOrder(order);

        final OrderStatusEntity initialStatus = new OrderStatusEntity();
        initialStatus.setIsActive(true);
        initialStatus.setStatus(orderAggregate.stateMachine.getInitial().getFrom());

        orderAggregate.statuses.add(initialStatus);
        return orderAggregate;
    }

    public static OrderAggregate create(
            OrderId orderId,
            OrderEntity order,
            List<OrderStatusEntity> statuses
    ) {
        final OrderAggregate orderAggregate = new OrderAggregate();
        orderAggregate.setId(orderId);
        orderAggregate.setOrder(order);
        orderAggregate.statuses = statuses.stream()
                                        .sorted(Comparator.comparing(OrderStatusEntity::getUpdatedAt))
                                        .collect(Collectors.toList());

        OrderStatusEntity currentState = statuses.get(statuses.size() - 1);
        orderAggregate.stateMachine.setState(currentState.getStatus());
        return orderAggregate;
    }

   public OrderStatusEntity getCurrentStatus() {
        Optional<OrderStatusEntity> currentState = statuses.stream()
                            .filter(orderStatus -> {
                                return orderStatus.getStatus().equals(stateMachine.getCurrentState().getFrom());
                            })
                            .findFirst();

        if(currentState.isEmpty()) {
            throw new RuntimeException("Current status not found.");
        }

        return currentState.get();
   }

   public OrderStatusEntity addStatus(OrderStatus.Event event) {
        final StateMachine.State<OrderStatus.Status, OrderStatus.Event> currentState = stateMachine.getCurrentState();

        if(currentState != null) {
            if(currentState.isFinalState()) {
                throw new OrderIsAlreadyInFinalStateException(getId(), currentState.getFrom());
            }

            if(!currentState.hasEvent(event)) {
                throw new OrderStateDoesNotSupportEventException(
                        getId(),
                        stateMachine.getCurrentState().getFrom(),
                        stateMachine.getCurrentState().getSupportedEvents()
                );
            }
        }

        final OrderStatus.Status newState = stateMachine.executeEvent(event);

        for (OrderStatusEntity statusEntity : statuses) {
            statusEntity.setIsActive(false);
        }

       OrderStatusEntity newOrderStatusEntity = new OrderStatusEntity();
       newOrderStatusEntity.setIsActive(true);
       newOrderStatusEntity.setStatus(newState);

       statuses.add(newOrderStatusEntity);

       return newOrderStatusEntity;
   }
}
