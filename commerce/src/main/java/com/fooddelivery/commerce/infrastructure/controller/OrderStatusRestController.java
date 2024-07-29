package com.fooddelivery.commerce.infrastructure.controller;

import com.fooddelivery.commerce.application.command.order.addorderstatus.AddOrderStatusCommand;
import com.fooddelivery.commerce.application.command.order.addorderstatus.AddOrderStatusResponse;
import com.fooddelivery.commerce.application.exception.order.OrderNotExistsException;
import com.fooddelivery.commerce.domain.order.exception.OrderIsAlreadyInFinalStateException;
import com.fooddelivery.commerce.domain.order.exception.OrderStateDoesNotSupportEventException;
import com.fooddelivery.commerce.domain.order.port.input.AddOrderStatusPort;
import com.fooddelivery.commerce.infrastructure.contract.order.addorderstatus.AddOrderStatusRequestDto;
import com.fooddelivery.commerce.infrastructure.contract.order.addorderstatus.AddOrderStatusResponseDto;
import com.fooddelivery.commerce.infrastructure.contract.order.getorder.GetOrderStatusResponseDto;
import com.fooddelivery.commerce.infrastructure.exception.order.EventIsNotSupportedHttpException;
import com.fooddelivery.commerce.infrastructure.exception.order.OrderIsAlreadyInFinalStateHttpException;
import com.fooddelivery.commerce.infrastructure.exception.order.OrderNotExistsHttpException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/statuses")
public class OrderStatusRestController {
    private AddOrderStatusPort addOrderStatusUseCase;

    OrderStatusRestController(AddOrderStatusPort addOrderStatusUseCase) {
        this.addOrderStatusUseCase = addOrderStatusUseCase;
    }
    @PutMapping(consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "New status is created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetOrderStatusResponseDto.class)
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
            @ApiResponse(
                    responseCode = "409",
                    description = "Current order state has not provided event.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EventIsNotSupportedHttpException.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Order is already in final state.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderIsAlreadyInFinalStateHttpException.class)
                    )
            ),
    })
    public ResponseEntity<?> createNewOrderStatus(
            @Valid @RequestBody AddOrderStatusRequestDto body,
            @PathVariable("orderId") Long orderId
    ) {
        final AddOrderStatusCommand command = new AddOrderStatusCommand(orderId, body.event());

        try {
            final AddOrderStatusResponse result = addOrderStatusUseCase.addStatus(command);
            final AddOrderStatusResponseDto dto = new AddOrderStatusResponseDto(result.newStatus());

            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (OrderIsAlreadyInFinalStateException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                                 .body(
                                         new OrderIsAlreadyInFinalStateHttpException(
                                                 orderId,
                                                 e.getFinalState()
                                         )
                                 );
        } catch (OrderStateDoesNotSupportEventException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new EventIsNotSupportedHttpException(e.getCurrentState(), e.getSupportedEvents()));
        } catch (OrderNotExistsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new OrderNotExistsHttpException(orderId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL SERVER ERROR");
        }
    }
}
