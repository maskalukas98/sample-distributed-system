package com.fooddelivery.commerce.infrastructure.contract.product.createproduct;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateProductResponseDto(
        @Schema(example = "268435457")
        int productId
) {}
