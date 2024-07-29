package com.fooddelivery.commerce.helper;

import com.fooddelivery.commerce.configuration.datasource.ShardSelection;
import com.fooddelivery.commerce.domain.partner.PartnerAggregate;
import com.fooddelivery.commerce.domain.partner.constant.Country;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TestPartnerRepositoryHelper {
    private final ShardSelection shardRouter;

    TestPartnerRepositoryHelper(ShardSelection shardRouter) {
        this.shardRouter = shardRouter;
    }

    public void clearAllTables(String country) {
        final JdbcTemplate jdbcTemplate = shardRouter.selectWriteShardByCountry(country);

        jdbcTemplate.update("TRUNCATE TABLE partners RESTART IDENTITY CASCADE");
    }

    public List<PartnerAggregate> getAllPartners(String country) {
        final JdbcTemplate jdbcTemplate = shardRouter.selectWriteShardByCountry(country);
        return jdbcTemplate.query("SELECT * FROM partners", partnerAggregateRowMapper);
    }

    private final RowMapper<PartnerAggregate> partnerAggregateRowMapper = (rs, rowNum) -> {
        PartnerAggregate partnerAggregate = new PartnerAggregate();
        partnerAggregate.setCompanyName(rs.getString("company_name"));
        partnerAggregate.setCountry(Country.valueOf(rs.getString("country")));
        return partnerAggregate;
    };
}
