package com.fooddelivery.commerce.infrastructure.repository.product;


import com.fooddelivery.commerce.application.exception.product.ProductNotExistsException;
import com.fooddelivery.commerce.configuration.datasource.ShardSelection;
import com.fooddelivery.commerce.domain.product.ProductAggregate;
import com.fooddelivery.commerce.domain.product.entity.ProductEntity;
import com.fooddelivery.commerce.domain.product.port.output.ProductReadRepositoryPort;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import com.fooddelivery.commerce.infrastructure.exception.db.UnableToFindShardException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
public class PostgresProductReadRepository implements ProductReadRepositoryPort {
    private final ShardSelection shardRouter;

    PostgresProductReadRepository(ShardSelection shardRouter) {
        this.shardRouter = shardRouter;
    }

    @Override
    public ProductAggregate findProductById(ProductId productId) {
        try {
            final JdbcTemplate jdbcTemplate = shardRouter.selectReadShardById(productId.getShardKey());
            final String sql = "SELECT * FROM products WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, productRowMapper, productId.getValue());
        } catch (EmptyResultDataAccessException | UnableToFindShardException e) {
            throw new ProductNotExistsException(productId);
        } catch (Exception e) {
            throw e;
        }
    }

    private final RowMapper<ProductAggregate> productRowMapper = (rs, rowNum) -> {
        final ProductId productId = new ProductId(rs.getInt("id"));

        final ProductAggregate productAggregate = new ProductAggregate();
        productAggregate.setId(productId);

        final ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setProductName(rs.getString("product_name"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setActive(rs.getBoolean("is_active"));
        product.setPartnerId(new PartnerId(rs.getInt("partner_id")));
        product.setCurrencyCode(rs.getString("currency_code"));

        productAggregate.setProduct(product);

        return productAggregate;
    };
}
