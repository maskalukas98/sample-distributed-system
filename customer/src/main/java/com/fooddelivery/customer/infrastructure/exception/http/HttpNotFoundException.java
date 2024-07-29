package com.fooddelivery.customer.infrastructure.exception.http;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpNotFoundException  {
    @Schema(example = "Customer with the ID 100 was not found.")
    private String message;
    public HttpNotFoundException(String message) {
        this.message = message;
    }
}
