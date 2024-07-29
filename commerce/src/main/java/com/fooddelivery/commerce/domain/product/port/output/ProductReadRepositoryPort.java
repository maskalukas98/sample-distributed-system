package com.fooddelivery.commerce.domain.product.port.output;

import com.fooddelivery.commerce.domain.product.ProductAggregate;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;

public interface ProductReadRepositoryPort {
    ProductAggregate findProductById(ProductId productId);
}
