package com.fooddelivery.customer.application.command;


public record CreateCustomerCommand (
    String name,
    String surname,
    String email
){}
