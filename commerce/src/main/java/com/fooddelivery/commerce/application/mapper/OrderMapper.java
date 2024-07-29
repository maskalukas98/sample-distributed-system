package com.fooddelivery.commerce.application.mapper;

import com.fooddelivery.commerce.application.command.order.createorder.CreateOrderCommand;
import com.fooddelivery.commerce.application.command.order.getorder.GetOrderResponse;
import com.fooddelivery.commerce.application.command.order.getorder.GetOrderStatusResponse;
import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.entity.OrderEntity;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import com.fooddelivery.commerce.infrastructure.contract.order.getorder.GetOrderResponseDto;
import com.fooddelivery.commerce.infrastructure.contract.order.getorder.GetOrderStatusResponseDto;


public final class OrderMapper {
    public static OrderAggregate mapFromCreateCommand(
            CreateOrderCommand command
    ) {
        final OrderEntity order = new OrderEntity();
        order.setCustomerId(command.customerId());
        order.setProductId(new ProductId(command.productId()));
        order.setAddress(command.address());

        return OrderAggregate.createNew(order);
    }

    public static GetOrderResponse mapFromAggregateToResponse(OrderAggregate orderAggregate) {
        final OrderEntity order = orderAggregate.getOrder();

        return new GetOrderResponse(
                orderAggregate.getId().getValue(),
                order.getCustomerId(),
                order.getProductId().getValue(),
                order.getAddress(),
                order.getCreatedAt(),
                orderAggregate.getStatuses().stream()
                        .map(s -> new GetOrderStatusResponse(
                                s.getId().getValue(),
                                s.getStatus(),
                                s.getIsActive(),
                                s.getUpdatedAt()
                        )).toList()
        );
    }

    public static GetOrderResponseDto mapFromCommandResponseToDto(GetOrderResponse response) {
        return new GetOrderResponseDto(
                response.id(),
                response.customerId(),
                response.productId(),
                response.address(),
                response.createdAt(),
                response.statuses().stream()
                        .map(s -> new GetOrderStatusResponseDto(
                                s.id(),
                                s.status(),
                                s.isActive(),
                                s.updatedAt()
                        )).toList()
        );
    }
}
