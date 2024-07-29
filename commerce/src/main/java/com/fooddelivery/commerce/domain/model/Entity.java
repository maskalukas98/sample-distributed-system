package com.fooddelivery.commerce.domain.model;

import lombok.Getter;

public abstract class Entity<Id> {
    @Getter
    protected Id id;

    public Entity<Id> setId(Id id) {
        this.id = id;
        return this;
    }
}
