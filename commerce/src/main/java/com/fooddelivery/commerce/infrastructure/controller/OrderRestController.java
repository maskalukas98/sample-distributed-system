package com.fooddelivery.commerce.infrastructure.controller;

import com.fooddelivery.commerce.application.command.order.createorder.CreateOrderCommand;
import com.fooddelivery.commerce.application.command.order.createorder.CreateOrderResponse;
import com.fooddelivery.commerce.application.command.order.getorder.GetOrderCommand;
import com.fooddelivery.commerce.application.command.order.getorder.GetOrderResponse;
import com.fooddelivery.commerce.application.exception.customer.CustomerNotExistsException;
import com.fooddelivery.commerce.application.exception.order.OrderNotExistsException;
import com.fooddelivery.commerce.application.exception.product.ProductNotExistsException;
import com.fooddelivery.commerce.application.mapper.OrderMapper;
import com.fooddelivery.commerce.domain.order.port.input.CreateOrderPort;
import com.fooddelivery.commerce.domain.order.port.input.GetOrderPort;
import com.fooddelivery.commerce.domain.product.exception.ProductIsNotActiveException;
import com.fooddelivery.commerce.infrastructure.contract.order.createorder.CreateOrderRequestDto;
import com.fooddelivery.commerce.infrastructure.contract.order.createorder.CreateOrderResponseDto;
import com.fooddelivery.commerce.infrastructure.contract.order.getorder.GetOrderResponseDto;
import com.fooddelivery.commerce.infrastructure.exception.http.InternalServerErrorHttpException;
import com.fooddelivery.commerce.infrastructure.exception.http.UnProcessableEntityException;
import com.fooddelivery.commerce.infrastructure.exception.order.OrderNotExistsHttpException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderRestController {
    private final CreateOrderPort createOrderUseCase;
    private final GetOrderPort getOrderUseCase;

    @Autowired
    OrderRestController(
            CreateOrderPort createOrderUseCase,
            GetOrderPort getOrderUseCase
    ) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping(consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateOrderResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "(Product|Partner|Customer) not exists.</br>" +
                                  "Product is not active.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UnProcessableEntityException.class)
                    )
            ),
    })
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateOrderRequestDto body) {
        final CreateOrderCommand command = new CreateOrderCommand(
                body.productId(),
                body.customerId(),
                body.address()
        );

        try {
            final CreateOrderResponse result = createOrderUseCase.create(command);
            final CreateOrderResponseDto dto = new CreateOrderResponseDto(
                    result.orderId(),
                    result.status()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (CustomerNotExistsException | ProductNotExistsException | ProductIsNotActiveException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new UnProcessableEntityException(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InternalServerErrorHttpException());
        }
    }

    @GetMapping("/{orderId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetOrderResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderNotExistsHttpException.class)
                    )
            ),
    })
    public ResponseEntity<?> getOrder(
            @PathVariable
            long orderId
    ) {
        final GetOrderCommand command = new GetOrderCommand(orderId);

        try {
            final GetOrderResponse result = getOrderUseCase.getById(command);
            final GetOrderResponseDto dto = OrderMapper.mapFromCommandResponseToDto(result);

            return ResponseEntity.ok(dto);
        } catch (OrderNotExistsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new OrderNotExistsHttpException(e.getOrderId().getValue()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InternalServerErrorHttpException());
        }
    }
}
