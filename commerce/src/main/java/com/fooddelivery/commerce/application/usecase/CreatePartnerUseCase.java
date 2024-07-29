package com.fooddelivery.commerce.application.usecase;

import com.fooddelivery.commerce.application.command.partner.createpartner.CreatePartnerCommand;
import com.fooddelivery.commerce.application.command.partner.createpartner.CreatePartnerResponse;
import com.fooddelivery.commerce.application.exception.partner.PartnerWithNameAlreadyExistsException;
import com.fooddelivery.commerce.domain.partner.PartnerAggregate;
import com.fooddelivery.commerce.domain.partner.constant.Country;
import com.fooddelivery.commerce.domain.partner.port.input.CreatePartnerPort;
import com.fooddelivery.commerce.domain.partner.port.output.PartnerReadRepositoryPort;
import com.fooddelivery.commerce.domain.partner.port.output.PartnerWriteRepositoryPort;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class CreatePartnerUseCase implements CreatePartnerPort {
    private final Logger logger = LoggerFactory.getLogger(CreatePartnerUseCase.class);
    private final PartnerWriteRepositoryPort partnerWriteRepository;
    private final PartnerReadRepositoryPort partnerReadRepository;

    @Autowired
    CreatePartnerUseCase(
            PartnerWriteRepositoryPort partnerWriteRepository,
            PartnerReadRepositoryPort partnerReadRepository
    ) {
        this.partnerWriteRepository = partnerWriteRepository;
        this.partnerReadRepository = partnerReadRepository;
    }

    @Override
    public CreatePartnerResponse create(CreatePartnerCommand command) {
        try {
            final PartnerAggregate newPartner = PartnerAggregate.createNew(command.companyName(), command.country());
            PartnerId newPartnerId = partnerWriteRepository.savePartner(newPartner);

            return new CreatePartnerResponse(newPartnerId.getValue());
        } catch (PartnerWithNameAlreadyExistsException e) {
            this.handleDuplicatePartnerException(e, command.country(), command.companyName());
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    private void handleDuplicatePartnerException(
            PartnerWithNameAlreadyExistsException e,
            Country country,
            String companyName
    ) {
        try {
            PartnerAggregate partner = partnerReadRepository.getPartnerByCompanyName(country, companyName);
            e.setConflictedPartnerId(partner.getId());
        } catch (Exception ex) {
            logger.error("Unable to fetch partner by company name '" + companyName + "' for HATEOAS.", ex);
        }
    }
}
