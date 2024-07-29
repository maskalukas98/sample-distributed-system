package com.fooddelivery.commerce.application.mapper;

import com.fooddelivery.commerce.application.command.product.createproduct.CreateProductCommand;
import com.fooddelivery.commerce.domain.product.ProductAggregate;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import com.fooddelivery.commerce.domain.product.entity.ProductEntity;

public final class ProductAggregateMapper {
    public static ProductAggregate mapFromCreateCommand(CreateProductCommand command) {
        ProductEntity product = new ProductEntity();
        product.setPartnerId(new PartnerId(command.partnerId()));
        product.setProductName(command.productName());
        product.setPrice(command.price());
        product.setCurrencyCode(command.currency());

        return ProductAggregate.createNew(product);
    }
}
