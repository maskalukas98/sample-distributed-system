package com.fooddelivery.customer.application.event;

public record RegistrationSuccessEvent(
        Integer customerId,
        String fullName,
        String email
) { }
