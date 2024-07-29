package com.fooddelivery.commerce.infrastructure.contract.order.addorderstatus;

import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record AddOrderStatusRequestDto(
        @Schema(example = "deliver")
        OrderStatus.Event event
) {}
