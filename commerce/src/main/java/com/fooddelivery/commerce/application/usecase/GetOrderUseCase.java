package com.fooddelivery.commerce.application.usecase;

import com.fooddelivery.commerce.application.command.order.getorder.GetOrderCommand;
import com.fooddelivery.commerce.application.command.order.getorder.GetOrderResponse;
import com.fooddelivery.commerce.application.mapper.OrderMapper;
import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.port.input.GetOrderPort;
import com.fooddelivery.commerce.domain.order.port.output.OrderReadRepositoryPort;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import org.springframework.stereotype.Service;

@Service
public class GetOrderUseCase implements GetOrderPort {
    private final OrderReadRepositoryPort orderReadRepository;

    public GetOrderUseCase(OrderReadRepositoryPort orderReadRepository) {
        this.orderReadRepository = orderReadRepository;
    }

    @Override
    public GetOrderResponse getById(GetOrderCommand command) {
        final OrderId orderId = new OrderId(command.orderId());
        OrderAggregate orderAggregate = orderReadRepository.findOrderById(orderId);

        return OrderMapper.mapFromAggregateToResponse(orderAggregate);
    }
}
