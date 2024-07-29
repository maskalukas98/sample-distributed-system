package com.fooddelivery.commerce.domain.order.valueobject;

import com.fooddelivery.commerce.domain.model.ValueObject;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderStatusId extends ValueObject {
    private final Long value;


    public OrderStatusId(Long id) {
        this.value = id;
    }
}
