package com.fooddelivery.commerce.application.command.product.createproduct;

import java.math.BigDecimal;

public record CreateProductCommand(
        int partnerId,
        String productName,
        BigDecimal price,
        String currency
) {}
