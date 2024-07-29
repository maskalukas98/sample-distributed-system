package com.fooddelivery.commerce.application.usecase;

import com.fooddelivery.commerce.application.command.product.createproduct.CreateProductCommand;
import com.fooddelivery.commerce.application.command.product.createproduct.CreateProductResponse;
import com.fooddelivery.commerce.application.exception.partner.PartnerNotExistsException;
import com.fooddelivery.commerce.application.mapper.ProductAggregateMapper;
import com.fooddelivery.commerce.domain.product.ProductAggregate;
import com.fooddelivery.commerce.domain.product.port.input.CreateProductPort;
import com.fooddelivery.commerce.domain.partner.port.output.PartnerReadRepositoryPort;
import com.fooddelivery.commerce.domain.product.port.output.ProductWriteRepositoryPort;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import org.springframework.stereotype.Service;

@Service
public class CreateProductUseCase implements CreateProductPort {
    private final PartnerReadRepositoryPort partnerReadRepository;
    private final ProductWriteRepositoryPort productWriteRepository;

    CreateProductUseCase(
            PartnerReadRepositoryPort partnerReadRepository,
            ProductWriteRepositoryPort productWriteRepository
    ) {
        this.partnerReadRepository = partnerReadRepository;
        this.productWriteRepository = productWriteRepository;
    }

    @Override
    public CreateProductResponse create(CreateProductCommand command) {
        final ProductAggregate newProductAggregate = ProductAggregateMapper.mapFromCreateCommand(command);
        final PartnerId partnerId = new PartnerId(command.partnerId());

        if(!partnerReadRepository.existsPartnerWithId(partnerId)) {
            throw new PartnerNotExistsException(partnerId);
        }

        final ProductId newProductId = productWriteRepository.saveProduct(newProductAggregate);

        return new CreateProductResponse(newProductId.getValue());
    }
}
