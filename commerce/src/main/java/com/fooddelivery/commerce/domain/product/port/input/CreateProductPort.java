package com.fooddelivery.commerce.domain.product.port.input;

import com.fooddelivery.commerce.application.command.product.createproduct.CreateProductCommand;
import com.fooddelivery.commerce.application.command.product.createproduct.CreateProductResponse;

public interface CreateProductPort {
    CreateProductResponse create(CreateProductCommand command);
}
