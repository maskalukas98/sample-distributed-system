package com.fooddelivery.commerce.domain.product.entity;

import com.fooddelivery.commerce.domain.model.Entity;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public final class ProductEntity extends Entity<ProductId> {
    private PartnerId partnerId;
    private String productName;
    private BigDecimal price;
    private boolean isActive;
    private String currencyCode;

    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Price cannot be lower than 0");
        }


        this.price = price;
    }
}
