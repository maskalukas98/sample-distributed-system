package com.fooddelivery.commerce.application.exception.partner;

public class FailedToSavePartnerException extends RuntimeException {
    private final Exception e;
    public FailedToSavePartnerException(String companyName, Exception e) {
        super("Failed to save partner " + companyName + ".");
        this.e = e;
    }
}
