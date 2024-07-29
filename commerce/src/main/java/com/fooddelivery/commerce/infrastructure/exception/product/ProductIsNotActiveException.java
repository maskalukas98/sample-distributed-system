package com.fooddelivery.commerce.infrastructure.exception.product;

import com.fooddelivery.commerce.domain.product.valueobject.ProductId;

public class ProductIsNotActiveException extends RuntimeException {
    public ProductIsNotActiveException(ProductId productId) {
        super("Product with id " + productId.getValue() + " is not active.");
    }
}
