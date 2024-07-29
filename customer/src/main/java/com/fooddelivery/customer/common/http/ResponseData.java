package com.fooddelivery.customer.common.http;

public record ResponseData<T> (
        T data
){}
