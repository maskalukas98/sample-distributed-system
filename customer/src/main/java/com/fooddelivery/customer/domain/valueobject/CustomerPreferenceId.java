package com.fooddelivery.customer.domain.valueobject;

import com.fooddelivery.customer.domain.models.ValueObject;
import lombok.Getter;

@Getter
public final class CustomerPreferenceId extends ValueObject {
    private final Integer value;

    private CustomerPreferenceId(int id) {
        this.value = id;
    }
}
