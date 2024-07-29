package com.fooddelivery.commerce.domain.partner.valueobject;


import com.fooddelivery.commerce.domain.model.ValueObject;
import lombok.Getter;

@Getter
public final class PartnerId extends ValueObject {
    private final Integer value;

    public PartnerId(int id) {
        this.value = id;
    }

    public int getShardKey() {
        return value >> 24;
    }

    public int getSeqId() {
        return ((1 << 24) - 1) & value;
    }
}
