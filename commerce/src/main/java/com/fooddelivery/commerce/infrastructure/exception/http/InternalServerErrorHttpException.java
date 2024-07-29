package com.fooddelivery.commerce.infrastructure.exception.http;

import lombok.Getter;

@Getter
public class InternalServerErrorHttpException {
    private final String message = "We're sorry, but something went wrong on our end. Please try again later.";
}
