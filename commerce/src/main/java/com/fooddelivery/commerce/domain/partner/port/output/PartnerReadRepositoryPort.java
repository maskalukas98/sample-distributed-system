package com.fooddelivery.commerce.domain.partner.port.output;

import com.fooddelivery.commerce.application.exception.partner.PartnerNotExistsException;
import com.fooddelivery.commerce.domain.partner.PartnerAggregate;
import com.fooddelivery.commerce.domain.partner.constant.Country;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;

public interface PartnerReadRepositoryPort {
    PartnerAggregate getPartnerByCompanyName(Country country, String companyName);
    PartnerAggregate getPartnerByProductId(ProductId productId) throws PartnerNotExistsException;
    boolean existsPartnerWithId(PartnerId partnerId);
}
