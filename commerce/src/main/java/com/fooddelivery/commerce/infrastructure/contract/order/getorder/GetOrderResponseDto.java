package com.fooddelivery.commerce.infrastructure.contract.order.getorder;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record GetOrderResponseDto(
        @Schema(example = "1935264867944450")
        long id,
        @Schema(example = "1")
        int customerId,
        @Schema(example = "268435458")
        int productId,
        @Schema(example = "Prague 11")
        String address,
        @Schema(example = "2023-10-26T14:25:12.376288")
        LocalDateTime createdAt,
        List<GetOrderStatusResponseDto> statuses
) { }
