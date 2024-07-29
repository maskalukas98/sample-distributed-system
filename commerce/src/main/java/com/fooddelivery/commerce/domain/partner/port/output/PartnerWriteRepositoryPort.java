package com.fooddelivery.commerce.domain.partner.port.output;

import com.fooddelivery.commerce.domain.partner.PartnerAggregate;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;

public interface PartnerWriteRepositoryPort {
    PartnerId savePartner(PartnerAggregate partner);
}
