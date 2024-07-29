package com.fooddelivery.commerce.infrastructure.contract.product.createproduct;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequestDto (
        @Schema(example = "268435457")
        @NotNull
        @Min(value = 1)
        int partnerId,
        @NotBlank
        @NotEmpty
        @Schema(example = "Hamburger", minLength = 1)
        String productName,
        @NotNull
        @Min(value = 0)
        @Schema(example = "20")
        BigDecimal price,
        @NotBlank
        @NotEmpty
        @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters long")
        @Schema(example = "EUR")
        String currency
){}
