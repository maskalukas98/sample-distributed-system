package com.fooddelivery.commerce.unit.application.usecase;

import com.fooddelivery.commerce.application.command.product.createproduct.CreateProductCommand;
import com.fooddelivery.commerce.application.command.product.createproduct.CreateProductResponse;
import com.fooddelivery.commerce.application.exception.partner.PartnerNotExistsException;
import com.fooddelivery.commerce.application.usecase.CreateProductUseCase;
import com.fooddelivery.commerce.domain.partner.port.output.PartnerReadRepositoryPort;
import com.fooddelivery.commerce.domain.product.port.output.ProductWriteRepositoryPort;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateProductUseCaseTest {
    @Mock
    private ProductWriteRepositoryPort productWriteRepository;
    @Mock
    private PartnerReadRepositoryPort partnerReadRepository;
    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    @Test
    public void testCreate_shouldThrowExceptionDueToNotExistsPartner() {
        // prepare
        when(partnerReadRepository.existsPartnerWithId(any())).thenReturn(false);
        final CreateProductCommand command = new CreateProductCommand(10, "Hamburger", new BigDecimal(10), "EUR");

        // execute + verify
        Assertions.assertThrows(PartnerNotExistsException.class, () -> {
            createProductUseCase.create(command);
        });
    }

    @Test
    public void testCreate_shouldCreatePartner() {
        // prepare
        when(partnerReadRepository.existsPartnerWithId(any())).thenReturn(true);
        when(productWriteRepository.saveProduct(any())).thenReturn(new ProductId(100));
        final CreateProductCommand command = new CreateProductCommand(10, "Hamburger", new BigDecimal(10), "EUR");

        // execute
        CreateProductResponse response = Assertions.assertDoesNotThrow(() -> {
            return createProductUseCase.create(command);
        });

        // verify
        Assertions.assertEquals(response.newProductId(), 100);
    }
}