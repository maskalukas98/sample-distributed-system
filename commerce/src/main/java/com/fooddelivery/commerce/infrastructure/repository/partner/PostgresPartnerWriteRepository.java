package com.fooddelivery.commerce.infrastructure.repository.partner;

import com.fooddelivery.commerce.application.exception.partner.FailedToSavePartnerException;
import com.fooddelivery.commerce.application.exception.partner.PartnerWithNameAlreadyExistsException;
import com.fooddelivery.commerce.configuration.datasource.ShardRouter;
import com.fooddelivery.commerce.domain.partner.PartnerAggregate;
import com.fooddelivery.commerce.domain.partner.port.output.PartnerWriteRepositoryPort;
import com.fooddelivery.commerce.domain.partner.valueobject.PartnerId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;

@Repository
public class PostgresPartnerWriteRepository implements PartnerWriteRepositoryPort {
    private final ShardRouter shardRouter;

    PostgresPartnerWriteRepository(ShardRouter shardRouter) {
        this.shardRouter = shardRouter;
    }

    @Override
    public PartnerId savePartner(PartnerAggregate partner) {
        final JdbcTemplate jdbcTemplate = shardRouter.selectWriteShardByCountry(partner.getShardKey());
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        int newPartnerId;

        try {
            jdbcTemplate.update(connection -> {
                String sql = "INSERT INTO partners (company_name, country) VALUES (?,?);";
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, partner.getCompanyName());
                ps.setObject(2, partner.getCountry().toString(), Types.OTHER);
                return ps;
            }, keyHolder);

            newPartnerId = keyHolder.getKey().intValue();
        } catch (DuplicateKeyException e) {
            if(e.getMessage().contains("partners_company_name_key")) {
                throw new PartnerWithNameAlreadyExistsException(partner.getCompanyName());
            }

            throw new FailedToSavePartnerException(partner.getCompanyName(), e);
        } catch (Exception e) {
            throw new FailedToSavePartnerException(partner.getCompanyName(), e);
        }

       return new PartnerId(newPartnerId);
    }
}
