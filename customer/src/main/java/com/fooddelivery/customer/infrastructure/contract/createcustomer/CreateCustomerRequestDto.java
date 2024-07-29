package com.fooddelivery.customer.infrastructure.contract.createcustomer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequestDto(
        @NotBlank
        @Size(min = 2, max = 20)
        @Schema(description = "The name of the customer", example = "John")
        String name,

        @NotBlank
        @Size(min = 2, max = 20)
        @Schema(description = "The surname of the customer", example = "Doe")
        String surname,

        @NotBlank
        @Email
        @Schema(example = "john@example.com")
        String email
){}
