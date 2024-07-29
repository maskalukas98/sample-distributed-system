package com.fooddelivery.commerce.domain.model;

import lombok.Getter;

public abstract class AggregateRoot<Id> {
    @Getter
    protected Id id;

    public AggregateRoot<Id> setId(Id id) {
        this.id = id;
        return this;
    }
}
