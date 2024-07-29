package com.fooddelivery.commerce.unit.application.usecase;

import com.fooddelivery.commerce.application.command.order.createorder.CreateOrderCommand;
import com.fooddelivery.commerce.application.command.order.createorder.CreateOrderResponse;
import com.fooddelivery.commerce.application.exception.customer.CustomerNotExistsException;
import com.fooddelivery.commerce.application.usecase.CreateOrderUseCase;
import com.fooddelivery.commerce.common.logger.BusinessLogger;
import com.fooddelivery.commerce.domain.order.port.output.OrderWriteRepositoryPort;
import com.fooddelivery.commerce.domain.order.valueobject.OrderId;
import com.fooddelivery.commerce.domain.partner.PartnerAggregate;
import com.fooddelivery.commerce.domain.partner.constant.Country;
import com.fooddelivery.commerce.domain.partner.port.output.PartnerReadRepositoryPort;
import com.fooddelivery.commerce.domain.port.output.CustomerServicePort;
import com.fooddelivery.commerce.domain.product.ProductAggregate;
import com.fooddelivery.commerce.domain.product.entity.ProductEntity;
import com.fooddelivery.commerce.domain.product.exception.ProductIsNotActiveException;
import com.fooddelivery.commerce.domain.product.port.output.ProductReadRepositoryPort;
import com.fooddelivery.commerce.infrastructure.service.queue.RabbitOrderProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CreateOrderUseCaseTest {
    @Mock
    private OrderWriteRepositoryPort orderWriteRepository;
    @Mock
    private ProductReadRepositoryPort productReadRepository;
    @Mock
    private CustomerServicePort customerService;
    @Mock
    private BusinessLogger businessLogger;
    @Mock
    private RabbitOrderProducer rabbitOrderProducer;
    @Mock
    private PartnerReadRepositoryPort partnerReadRepository;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    @Test
    public void testCreate_shouldThrowExceptionDueToNotExistsCustomer() {
        // prepare
        when(customerService.customerExistsById(10)).thenReturn(false);

        // execute + verify
        Assertions.assertThrows(CustomerNotExistsException.class, () -> {
            createOrderUseCase.create(new CreateOrderCommand(1, 10,"Prague 11"));
        });
    }

    @Test
    public void testCreate_shouldThrowExceptionDueToNotActiveProduct() {
        // prepare
        final ProductEntity product = new ProductEntity();
        product.setActive(false);

        final ProductAggregate productAggregate = new ProductAggregate();
        productAggregate.setProduct(product);

        when(customerService.customerExistsById(10)).thenReturn(true);
        when(productReadRepository.findProductById(any()))
                .thenReturn(productAggregate);

        // execute + verify
        Assertions.assertThrows(ProductIsNotActiveException.class, () -> {
            createOrderUseCase.create(new CreateOrderCommand(1, 10,"Prague 11"));
        });
    }

    @Test
    public void testCreate_shouldCreateOrder() {
        // prepare
        final ProductEntity product = new ProductEntity();
        product.setActive(true);

        final ProductAggregate productAggregate = new ProductAggregate();
        productAggregate.setProduct(product);

        when(customerService.customerExistsById(10)).thenReturn(true);
        when(productReadRepository.findProductById(any()))
                .thenReturn(productAggregate);
        when(partnerReadRepository.getPartnerByProductId(any()))
                .thenReturn(PartnerAggregate.createNew("Fast pizza delivery", Country.fr));
        when(orderWriteRepository.saveNewOrder(any()))
                .thenReturn(new OrderId(500L));


        // execute
        CreateOrderResponse response = Assertions.assertDoesNotThrow(() -> {
            return createOrderUseCase.create(new CreateOrderCommand(1, 10,"Prague 11"));
        });

        // verify
        Assertions.assertTrue(response.orderId() > 0);
        Assertions.assertEquals(response.status().toString(), "created");

    }
}
