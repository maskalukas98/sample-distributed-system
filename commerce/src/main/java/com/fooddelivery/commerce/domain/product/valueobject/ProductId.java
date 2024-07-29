package com.fooddelivery.commerce.domain.product.valueobject;

import com.fooddelivery.commerce.domain.model.ValueObject;



public final class ProductId extends ValueObject {
    private final Integer value;

    public ProductId(int id) {
        this.value = id;
    }

    public Integer getValue() {
        return value;
    }

    public int getShardKey() {
        return value >> 24;
    }

    public int getSeqId() {
        return ((1 << 24) - 1) & value;
    }
}
