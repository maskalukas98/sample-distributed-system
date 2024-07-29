package com.fooddelivery.commerce.application.exception.product;

import com.fooddelivery.commerce.domain.product.valueobject.ProductId;

public class ProductNotExistsException extends RuntimeException {
    public ProductNotExistsException(ProductId productId) {
        super("Product with id " + productId.getValue() + " not exists.");
    }
}