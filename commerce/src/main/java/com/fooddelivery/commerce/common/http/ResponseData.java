package com.fooddelivery.commerce.common.http;


public record ResponseData<T> (
        T data
){}
