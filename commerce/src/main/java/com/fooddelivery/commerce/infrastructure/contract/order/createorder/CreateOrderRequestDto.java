package com.fooddelivery.commerce.infrastructure.contract.order.createorder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CreateOrderRequestDto(
        @Min(value = 1)
        @Schema(example = "268435457")
        int productId,
        @Min(value = 1)
        @Schema(example = "1")
        int customerId,
        @NotBlank
        @NotEmpty
        @Schema(example = "Prague 11", minLength = 5)
        String address
) {}
