package com.fooddelivery.commerce.helper;

import com.fooddelivery.commerce.configuration.datasource.ShardSelection;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import com.fooddelivery.commerce.domain.product.ProductAggregate;
import com.fooddelivery.commerce.domain.product.entity.ProductEntity;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class TestProductRepositoryHelper {
    private final ShardSelection shardRouter;

    TestProductRepositoryHelper(ShardSelection shardRouter) {
        this.shardRouter = shardRouter;
    }


    public List<ProductAggregate> getAllProducts(String country) {
        final JdbcTemplate jdbcTemplate = shardRouter.selectWriteShardByCountry(country);
        return jdbcTemplate.query("SELECT * FROM products", productAggregateRowMapper);
    }

    private final RowMapper<ProductAggregate> productAggregateRowMapper = (rs, rowNum) -> {
        final ProductEntity product = new ProductEntity();
        product.setId(new ProductId(rs.getInt("id")));
        product.setProductName(rs.getString("product_name"));
        product.setActive(rs.getBoolean("is_active"));
        product.setPrice(new BigDecimal(String.valueOf(rs.getBigDecimal("price"))));
        product.setPartnerId(new PartnerId(rs.getInt("partner_id")));
        product.setCurrencyCode(rs.getString("currency_code"));

        final ProductAggregate productAggregate = new ProductAggregate();
        productAggregate.setId(product.getId());
        productAggregate.setProduct(product);

        return productAggregate;
    };
}
