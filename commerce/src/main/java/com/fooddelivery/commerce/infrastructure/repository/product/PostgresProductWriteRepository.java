package com.fooddelivery.commerce.infrastructure.repository.product;

import com.fooddelivery.commerce.application.exception.common.DataIntegrityException;
import com.fooddelivery.commerce.application.exception.partner.PartnerNotExistsException;
import com.fooddelivery.commerce.configuration.datasource.ShardSelection;
import com.fooddelivery.commerce.domain.product.ProductAggregate;
import com.fooddelivery.commerce.domain.product.entity.ProductEntity;
import com.fooddelivery.commerce.domain.product.port.output.ProductWriteRepositoryPort;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

@Repository
public class PostgresProductWriteRepository implements ProductWriteRepositoryPort {
    private final ShardSelection shardRouter;

    PostgresProductWriteRepository(ShardSelection shardRouter) {
        this.shardRouter = shardRouter;
    }

    @Override
    public ProductId saveProduct(ProductAggregate productAggregate) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final PartnerId partnerId = productAggregate.getProduct().getPartnerId();
        final ProductEntity productEntity = productAggregate.getProduct();
        int newProductId;

        try {
            JdbcTemplate jdbcTemplate = shardRouter.selectWriteShardById(partnerId.getShardKey());
            jdbcTemplate.update(connection -> {
                String sql = "INSERT INTO products (product_name, price, partner_id, currency_code) VALUES (?,?,?,?);";
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, productEntity.getProductName());
                ps.setBigDecimal(2, productEntity.getPrice());
                ps.setInt(3, partnerId.getValue());
                ps.setString(4, productEntity.getCurrencyCode());
                return ps;
            }, keyHolder);

            newProductId = keyHolder.getKey().intValue();
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("products_partner_id_fkey")) {
                throw new PartnerNotExistsException(partnerId);
            }

            throw new DataIntegrityException(e);
        }

        return new ProductId(newProductId);
    }
}
