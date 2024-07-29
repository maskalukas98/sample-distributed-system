package com.fooddelivery.commerce.infrastructure.exception.http;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnProcessableEntityException {
    private String message;
    public UnProcessableEntityException(String message) {
        this.message = message;
    }
}
