package com.fooddelivery.commerce.domain.partner.port.input;

import com.fooddelivery.commerce.application.command.partner.createpartner.CreatePartnerCommand;
import com.fooddelivery.commerce.application.command.partner.createpartner.CreatePartnerResponse;

public interface CreatePartnerPort {
    CreatePartnerResponse create(CreatePartnerCommand command);
}
