package com.fooddelivery.commerce.infrastructure.contract.order.getorder;

import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record GetOrderStatusResponseDto (
        @Schema(example = "1935264867944450")
        long id,

        @Schema(example = "created")
        OrderStatus.Status status,
        @Schema(example = "true")
        boolean isActive,
        @Schema(example = "2023-10-26T14:25:12.376288")
        LocalDateTime updatedAt
){}
