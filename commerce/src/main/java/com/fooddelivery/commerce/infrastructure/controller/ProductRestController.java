package com.fooddelivery.commerce.infrastructure.controller;

import com.fooddelivery.commerce.application.command.product.createproduct.CreateProductCommand;
import com.fooddelivery.commerce.application.command.product.createproduct.CreateProductResponse;
import com.fooddelivery.commerce.application.exception.partner.PartnerNotExistsException;
import com.fooddelivery.commerce.domain.product.port.input.CreateProductPort;
import com.fooddelivery.commerce.infrastructure.contract.product.createproduct.CreateProductRequestDto;
import com.fooddelivery.commerce.infrastructure.contract.product.createproduct.CreateProductResponseDto;
import com.fooddelivery.commerce.infrastructure.exception.http.InternalServerErrorHttpException;
import com.fooddelivery.commerce.infrastructure.exception.http.UnProcessableEntityException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public final class ProductRestController {
    private final CreateProductPort createProductPort;

    @Autowired
    ProductRestController(CreateProductPort createProductPort) {
        this.createProductPort = createProductPort;
    }

    @PostMapping(consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateProductResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Partner not exists.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UnProcessableEntityException.class)
                    )
            ),
    })
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequestDto body) {
        CreateProductCommand command = new CreateProductCommand(
                body.partnerId(),
                body.productName(),
                body.price(),
                body.currency()
        );

        try {
            CreateProductResponse result = createProductPort.create(command);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new CreateProductResponseDto(result.newProductId()));
        } catch (PartnerNotExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new UnProcessableEntityException(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InternalServerErrorHttpException());
        }
    }
}
