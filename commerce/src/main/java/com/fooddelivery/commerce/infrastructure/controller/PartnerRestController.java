package com.fooddelivery.commerce.infrastructure.controller;

import com.fooddelivery.commerce.application.command.partner.createpartner.CreatePartnerCommand;
import com.fooddelivery.commerce.application.command.partner.createpartner.CreatePartnerResponse;
import com.fooddelivery.commerce.application.exception.partner.PartnerWithNameAlreadyExistsException;
import com.fooddelivery.commerce.domain.partner.port.input.CreatePartnerPort;
import com.fooddelivery.commerce.infrastructure.contract.partner.createpartner.CreatePartnerRequestDto;
import com.fooddelivery.commerce.infrastructure.contract.partner.createpartner.CreatePartnerResponseDto;

import com.fooddelivery.commerce.infrastructure.exception.http.InternalServerErrorHttpException;
import com.fooddelivery.commerce.infrastructure.exception.partner.PartnerConflictHttpException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/v1/partners")
public class PartnerRestController {
    private final CreatePartnerPort createPartnerPort;

    @Autowired
    PartnerRestController(
            CreatePartnerPort createPartnerPort
    ) {
        this.createPartnerPort = createPartnerPort;
    }

    @PostMapping(consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Partner created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreatePartnerResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Partner is already created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartnerConflictHttpException.class)
                    )
            ),
    })
    public ResponseEntity<?> createPartner(@Valid @RequestBody CreatePartnerRequestDto body) {
        final CreatePartnerCommand command = new CreatePartnerCommand(body.companyName(), body.country());

        try {
            final CreatePartnerResponse result = createPartnerPort.create(command);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new CreatePartnerResponseDto(result.newPartnerId()));
        } catch (PartnerWithNameAlreadyExistsException e) {
            final PartnerConflictHttpException res = new PartnerConflictHttpException(e.getCompanyName());

            if(e.getConflictedPartnerId() != null) {
                res.setLink(linkTo(PartnerRestController.class)
                        .slash(e.getConflictedPartnerId().getValue().toString())
                        .withSelfRel());
            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InternalServerErrorHttpException());
        }
    }

    @GetMapping(path = "/{partnerId}")
    @ApiResponses(value = { @ApiResponse( responseCode = "501" ) })
    public ResponseEntity<?> getPartner(@PathVariable int partnerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
