package com.fooddelivery.commerce.domain.order.valueobject;

import com.fooddelivery.commerce.domain.model.ValueObject;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class OrderId extends ValueObject {
    @Getter
    private final Long value;

    private final LocalDateTime dateTime;

    public OrderId(Long id) {
        this.value = id;
        this.dateTime = Instant.ofEpochMilli(getTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Long getTimestamp() {
        return  ((1L << 41L)-1L & (value >> 23)) + 1698105600011L;
    }

    public int getShardKey() {
        return (int) ((value >> 10) & ((1 << 10)-1));
    }
}
