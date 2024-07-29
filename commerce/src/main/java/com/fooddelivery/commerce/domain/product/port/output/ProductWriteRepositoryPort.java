package com.fooddelivery.commerce.domain.product.port.output;

import com.fooddelivery.commerce.application.exception.common.DataIntegrityException;
import com.fooddelivery.commerce.application.exception.partner.PartnerNotExistsException;
import com.fooddelivery.commerce.domain.product.ProductAggregate;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;

public interface ProductWriteRepositoryPort {
    ProductId saveProduct(ProductAggregate product) throws PartnerNotExistsException, DataIntegrityException;
}
