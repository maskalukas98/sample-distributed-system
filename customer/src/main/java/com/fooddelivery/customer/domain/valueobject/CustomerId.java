package com.fooddelivery.customer.domain.valueobject;

import com.fooddelivery.customer.domain.models.ValueObject;
import lombok.Getter;
import org.springframework.data.relational.core.sql.In;

public final class CustomerId extends ValueObject {
    private final Integer value;

    public CustomerId(int id) {
        this.value = id;
    }

    public Integer getValue() {
        return value;
    }
}
