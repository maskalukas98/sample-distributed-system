package com.fooddelivery.commerce.application.exception.partner;

import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import lombok.Getter;
import lombok.Setter;

public class PartnerWithNameAlreadyExistsException extends RuntimeException {
    @Getter
    private String companyName;

    @Setter
    @Getter
    private PartnerId conflictedPartnerId;
    public PartnerWithNameAlreadyExistsException(String companyName) {
        super("Partner with company name " + companyName + " exists.");
        this.companyName = companyName;
    }
}
