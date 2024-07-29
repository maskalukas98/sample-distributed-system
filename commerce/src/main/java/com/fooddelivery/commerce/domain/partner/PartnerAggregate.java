package com.fooddelivery.commerce.domain.partner;

import com.fooddelivery.commerce.domain.partner.constant.Country;
import com.fooddelivery.commerce.domain.model.AggregateRoot;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import lombok.Getter;
import lombok.Setter;

@Getter
public final class PartnerAggregate extends AggregateRoot<PartnerId> {
    @Setter
    private String companyName;

    @Setter
    private Country country;

    public static PartnerAggregate createNew(String companyName, Country country) {
        PartnerAggregate newPartner = new PartnerAggregate();
        newPartner.setCompanyName(companyName);
        newPartner.setCountry(country);
        return newPartner;
    }

    public static PartnerAggregate create(PartnerId id, String companyName, Country country) {
        PartnerAggregate newPartner = new PartnerAggregate();
        newPartner.setId(id);
        newPartner.setCompanyName(companyName);
        newPartner.setCountry(country);
        return newPartner;
    }

    public String getShardKey() {
        return String.valueOf(country);
    }
}
