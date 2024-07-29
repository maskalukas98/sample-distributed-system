package com.fooddelivery.commerce.infrastructure.contract.order.createorder;

import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateOrderResponseDto (
        @Schema(example = "1935264867944450")
        long orderId,
        @Schema(example = "created")
        OrderStatus.Status status
){}
