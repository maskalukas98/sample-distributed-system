package com.fooddelivery.commerce.application.exception.partner;

import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;

public class PartnerNotExistsException extends RuntimeException {
    public PartnerNotExistsException(PartnerId partnerId) {
        super("Partner with id " + partnerId.getValue() + " not exists.");
    }

    public PartnerNotExistsException(ProductId productId) {
        super("Partner for product with id " + productId.getValue() + " not exists.");
    }
}
