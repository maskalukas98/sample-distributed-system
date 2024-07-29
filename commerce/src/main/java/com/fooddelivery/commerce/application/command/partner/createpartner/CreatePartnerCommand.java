package com.fooddelivery.commerce.application.command.partner.createpartner;

import com.fooddelivery.commerce.domain.partner.constant.Country;

public record CreatePartnerCommand (
        String companyName,
        Country country
){}
