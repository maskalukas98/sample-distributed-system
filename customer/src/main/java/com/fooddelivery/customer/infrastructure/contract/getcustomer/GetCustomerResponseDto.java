package com.fooddelivery.customer.infrastructure.contract.getcustomer;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetCustomerResponseDto (
        @Schema(example = "100")
        int id,

        @Schema(example = "John")
        String name,

        @Schema(example = "Doe")
        String surname,

        @Schema(example = "john@example.com")
        String email
){}
