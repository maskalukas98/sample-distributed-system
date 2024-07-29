package com.fooddelivery.commerce.infrastructure.contract.partner.createpartner;

import com.fooddelivery.commerce.domain.partner.constant.Country;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record CreatePartnerRequestDto (
        @NotNull
        @NotBlank
        @Size(min = 1, max = 50)
        @Schema(example = "Fast pizza delivery")
        String companyName,

        @Schema(example = "fr")
        Country country
){}
