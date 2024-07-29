package com.fooddelivery.commerce.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.commerce.application.command.order.addorderstatus.AddOrderStatusCommand;
import com.fooddelivery.commerce.application.command.order.addorderstatus.AddOrderStatusResponse;
import com.fooddelivery.commerce.common.logger.BusinessLogger;
import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.entity.OrderStatusEntity;
import com.fooddelivery.commerce.domain.order.event.OrderStatusChangeEvent;
import com.fooddelivery.commerce.domain.order.port.input.AddOrderStatusPort;
import com.fooddelivery.commerce.domain.order.port.output.OrderReadRepositoryPort;
import com.fooddelivery.commerce.domain.order.port.output.OrderWriteRepositoryPort;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import com.fooddelivery.commerce.infrastructure.service.queue.RabbitOrderProducer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AddOrderStatusUseCase implements AddOrderStatusPort {
    private final BusinessLogger businessLogger;
    private final OrderReadRepositoryPort orderReadRepository;
    private final OrderWriteRepositoryPort orderWriteRepository;
    private RabbitOrderProducer rabbitOrderProducer;

    AddOrderStatusUseCase(
            OrderReadRepositoryPort orderReadRepository,
            OrderWriteRepositoryPort orderWriteRepository,
            BusinessLogger businessLogger,
            RabbitOrderProducer rabbitOrderProducer
    ) {
        this.orderReadRepository = orderReadRepository;
        this.orderWriteRepository = orderWriteRepository;
        this.businessLogger = businessLogger;
        this.rabbitOrderProducer = rabbitOrderProducer;
    }

    public AddOrderStatusResponse addStatus(AddOrderStatusCommand command) {
        final OrderId orderId = new OrderId(command.orderId());
        final OrderAggregate orderAggregate = orderReadRepository.findOrderById(orderId);
        final OrderStatusEntity newStatusEntity = orderAggregate.addStatus(command.event());

        orderWriteRepository.changeStatus(orderId, newStatusEntity.getStatus());

        try {
            rabbitOrderProducer.pushOrderChangedEvent(
                    newStatusEntity.getStatus().toString(),
                    new OrderStatusChangeEvent(
                            orderId.getValue(),
                            newStatusEntity.getStatus().toString()
                    )
            );
        } catch (Exception e) {
            // TODO: Implement event persistence, similar in the customer microservice,
            //       by saving events to the database for cron.
        }

        businessLogger.logInfo(
                "status_changed",
                String.valueOf(
                        new ObjectMapper().createObjectNode()
                                .put("order_id", orderId.getValue())
                                // TODO: Get time from the database
                                .put("updated_at", LocalDateTime.now().toString())
                                .put("new_status", newStatusEntity.getStatus().toString())
                )
        );

        return new AddOrderStatusResponse(newStatusEntity.getStatus());
    }
}
