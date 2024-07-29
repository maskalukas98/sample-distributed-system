package com.fooddelivery.customer.infrastructure.controller;

import com.fooddelivery.customer.application.command.CreateCustomerCommand;
import com.fooddelivery.customer.application.command.GetCustomerCommand;
import com.fooddelivery.customer.application.exception.CustomerNotFoundException;
import com.fooddelivery.customer.application.exception.DuplicateCustomerException;
import com.fooddelivery.customer.application.mapper.CreateCustomerMapper;
import com.fooddelivery.customer.common.http.ResponseData;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.port.input.CreateCustomerPort;
import com.fooddelivery.customer.domain.port.input.GetCustomerPort;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import com.fooddelivery.customer.infrastructure.contract.createcustomer.CreateCustomerRequestDto;
import com.fooddelivery.customer.infrastructure.contract.createcustomer.CreateCustomerResponseDto;
import com.fooddelivery.customer.infrastructure.contract.getcustomer.GetCustomerResponseDto;
import com.fooddelivery.customer.infrastructure.exception.http.HttpConflictException;
import com.fooddelivery.customer.infrastructure.exception.http.HttpNotFoundException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RestController
@RequestMapping("/api/v1/customers")
public final class CustomerRestController {
    private final CreateCustomerPort createUserPort;
    private final GetCustomerPort getCustomerPort;

    @Autowired
    CustomerRestController(
            CreateCustomerPort createUserPort,
            GetCustomerPort getCustomerPort
    ) {
        this.createUserPort = createUserPort;
        this.getCustomerPort = getCustomerPort;
    }

    @PostMapping(consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateCustomerResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Customer is already created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HttpConflictException.class)
                    )
            )
    })
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CreateCustomerRequestDto body) {
        CreateCustomerCommand command = CreateCustomerMapper.createUserRequestToCommand(body);

        try {
            CustomerId newCustomerId = createUserPort.create(command);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseData<>(new CreateCustomerResponseDto(newCustomerId.getValue())));
        } catch (DuplicateCustomerException e) {
            HttpConflictException res = new HttpConflictException(e.getMessage());
            res.setLink(linkTo(CustomerRestController.class).slash(e.getConflictedUserId().getValue()).withSelfRel());

            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{customerId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetCustomerResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HttpNotFoundException.class)
                    )
            ),
    })
    public ResponseEntity<?> getCustomer(
            @Parameter(
                    description = "The unique identifier of the customer",
                    example = "100",
                    required = true
            )
            @PathVariable
            int customerId
    ) {
        GetCustomerCommand command = new GetCustomerCommand(customerId);

        try {
            Customer customer = getCustomerPort.get(command);
            GetCustomerResponseDto dto = new GetCustomerResponseDto(
                    customer.getId().getValue(),
                    customer.getName(),
                    customer.getSurname(),
                    customer.getEmail()
            );

            return ResponseEntity.ok(new ResponseData<>(dto));
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpNotFoundException(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
