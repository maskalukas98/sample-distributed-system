package com.fooddelivery.commerce.infrastructure.repository.partner;

import com.fooddelivery.commerce.application.exception.partner.PartnerNotExistsException;
import com.fooddelivery.commerce.configuration.datasource.ShardSelection;
import com.fooddelivery.commerce.domain.partner.PartnerAggregate;
import com.fooddelivery.commerce.domain.partner.constant.Country;
import com.fooddelivery.commerce.domain.partner.port.output.PartnerReadRepositoryPort;
import com.fooddelivery.commerce.domain.product.valueobject.ProductId;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import com.fooddelivery.commerce.infrastructure.exception.db.UnableToFindShardException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresPartnerReadRepository implements PartnerReadRepositoryPort {
    private final ShardSelection shardRouter;

    PostgresPartnerReadRepository(ShardSelection shardRouter) {
        this.shardRouter = shardRouter;
    }

    @Override
    public PartnerAggregate getPartnerByCompanyName(Country country, String companyName) {

        try {
            JdbcTemplate jdbcTemplate = shardRouter.selectReadShardByCountry(country.toString());
            String sql = "SELECT * FROM partners WHERE company_name = ?";
            return jdbcTemplate.queryForObject(sql, partnerRowMapper, companyName);
        } catch (EmptyResultDataAccessException | UnableToFindShardException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public PartnerAggregate getPartnerByProductId(ProductId productId) {

        try {
            final JdbcTemplate jdbcTemplate = shardRouter.selectReadShardById(productId.getShardKey());
            final String sql = "SELECT pa.id, pa.company_name, pa.country " +
                               "FROM partners as pa " +
                               "JOIN public.products pr ON pa.id = pr.partner_id " +
                               "WHERE pr.id = ?;";

            return jdbcTemplate.queryForObject(sql, partnerRowMapper, productId.getValue());
        } catch (EmptyResultDataAccessException | UnableToFindShardException e) {
            throw new PartnerNotExistsException(productId);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean existsPartnerWithId(PartnerId partnerId) {
        try {
            final JdbcTemplate jdbcTemplate = shardRouter.selectReadShardById(partnerId.getShardKey());
            jdbcTemplate.queryForObject("SELECT 1 FROM partners WHERE id = ?", Integer.class, partnerId.getValue());
            return true;
        } catch (EmptyResultDataAccessException | UnableToFindShardException e) {
            return false;
        } catch (Exception e) {
            throw e;
        }
    }

    private final RowMapper<PartnerAggregate> partnerRowMapper = (rs, rowNum) -> {
        PartnerAggregate partner = new PartnerAggregate();
        partner.setId(new PartnerId(rs.getInt("id")));
        partner.setCountry(Country.valueOf(rs.getString("country")));
        partner.setCompanyName(rs.getString("company_name"));
        return partner;
    };
}
