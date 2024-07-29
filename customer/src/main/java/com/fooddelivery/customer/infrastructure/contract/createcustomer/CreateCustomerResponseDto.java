package com.fooddelivery.customer.infrastructure.contract.createcustomer;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateCustomerResponseDto(
        @Schema(example = "1")
        int userId
){}
