package com.fooddelivery.commerce.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.commerce.application.command.order.createorder.CreateOrderCommand;
import com.fooddelivery.commerce.application.command.order.createorder.CreateOrderResponse;
import com.fooddelivery.commerce.application.exception.customer.CustomerNotExistsException;
import com.fooddelivery.commerce.application.mapper.OrderMapper;
import com.fooddelivery.commerce.common.logger.BusinessLogger;
import com.fooddelivery.commerce.domain.partner.PartnerAggregate;
import com.fooddelivery.commerce.domain.port.output.CustomerServicePort;
import com.fooddelivery.commerce.domain.product.ProductAggregate;
import com.fooddelivery.commerce.domain.product.exception.ProductIsNotActiveException;
import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.event.OrderStatusChangeEvent;
import com.fooddelivery.commerce.domain.order.port.input.CreateOrderPort;
import com.fooddelivery.commerce.domain.order.port.output.OrderWriteRepositoryPort;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import com.fooddelivery.commerce.domain.partner.port.output.PartnerReadRepositoryPort;
import com.fooddelivery.commerce.domain.product.port.output.ProductReadRepositoryPort;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import com.fooddelivery.commerce.infrastructure.service.queue.RabbitOrderProducer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public final class CreateOrderUseCase implements CreateOrderPort {
    private final BusinessLogger businessLogger;
    private final OrderWriteRepositoryPort orderWriteRepository;
    private final ProductReadRepositoryPort productReadRepository;
    private final PartnerReadRepositoryPort partnerReadRepository;
    private final RabbitOrderProducer rabbitOrderProducer;
    private final CustomerServicePort customerService;


    CreateOrderUseCase(
            OrderWriteRepositoryPort orderWriteRepository,
            ProductReadRepositoryPort productReadRepository,
            PartnerReadRepositoryPort partnerReadRepository,
            BusinessLogger businessLogger,
            RabbitOrderProducer rabbitOrderProducer,
            CustomerServicePort customerService
    ) {
        this.orderWriteRepository = orderWriteRepository;
        this.productReadRepository = productReadRepository;
        this.partnerReadRepository = partnerReadRepository;
        this.businessLogger = businessLogger;
        this.rabbitOrderProducer = rabbitOrderProducer;
        this.customerService = customerService;
    }

    @Override
    public CreateOrderResponse create(CreateOrderCommand command) {
        if(!customerService.customerExistsById(command.customerId())) {
            throw new CustomerNotExistsException(command.customerId());
        }

        final OrderAggregate newOrderAggregate = OrderMapper.mapFromCreateCommand(command);
        final ProductId productId = newOrderAggregate.getOrder().getProductId();
        final ProductAggregate productAggregate = productReadRepository.findProductById(productId);

        if(!productAggregate.getProduct().isActive()) {
            throw new ProductIsNotActiveException(productId);
        }

        final PartnerAggregate partnerAggregate = partnerReadRepository.getPartnerByProductId(productId);
        final OrderId orderId = orderWriteRepository.saveNewOrder(newOrderAggregate);

        try {
            rabbitOrderProducer.pushOrderChangedEvent(
                    "created",
                    new OrderStatusChangeEvent(
                            orderId.getValue(),
                            newOrderAggregate.getCurrentStatus().getStatus().toString()
                    )
            );
        } catch (Exception e) {
            // TODO: Implement event persistence, similar to the customer microservice,
            //       by saving events to the database
        }

        businessLogger.logInfo(
                "order_created",
                String.valueOf(
                        new ObjectMapper().createObjectNode()
                            .put("order_id", orderId.getValue())
                             // TODO: Get date from the database
                            .put("created_at", LocalDateTime.now().toString())
                            .put("country", partnerAggregate.getCountry().toString())
                )
        );

        return new CreateOrderResponse(
                orderId.getValue(),
                newOrderAggregate.getCurrentStatus().getStatus()
        );
    }
}
