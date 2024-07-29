package com.fooddelivery.commerce.domain.product;

import com.fooddelivery.commerce.domain.model.AggregateRoot;
import com.fooddelivery.commerce.domain.product.entity.CurrencyEntity;
import com.fooddelivery.commerce.domain.product.entity.ProductEntity;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ProductAggregate extends AggregateRoot<ProductId> {
    private ProductEntity product;
    private CurrencyEntity currency;

    public static ProductAggregate createNew(ProductEntity product) {
        ProductAggregate productAggregate = new ProductAggregate();
        productAggregate.setProduct(product);
        return productAggregate;
    }
}
