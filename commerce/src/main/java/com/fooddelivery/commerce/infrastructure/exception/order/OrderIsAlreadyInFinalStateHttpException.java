package com.fooddelivery.commerce.infrastructure.exception.order;

import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderIsAlreadyInFinalStateHttpException  {

    @Schema(example = "created")
    private OrderStatus.Status finalState;
    private String message;

    public OrderIsAlreadyInFinalStateHttpException(long orderId, OrderStatus.Status finalState) {
        this.finalState = finalState;
        this.message = "Order " + orderId + " is already in final state.";
    }
}
