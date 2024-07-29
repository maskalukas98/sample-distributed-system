package com.fooddelivery.commerce.configuration.datasource;

import com.fooddelivery.commerce.infrastructure.exception.db.UnableToFindShardException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ShardRouter implements ShardSelection {
    private final Map<String, ShardGroup> shardJdbcTemplateListByCountry;
    private final Map<Integer, ShardGroup> shardJdbcTemplateListByIds;

    @Autowired
    public ShardRouter(
            @Qualifier("shardsByCountry")
            Map<String, ShardGroup> shardJdbcTemplateListByCountry,
            @Qualifier("shardsByIds")
            Map<Integer, ShardGroup> shardJdbcTemplateListByIds
    ) {
        this.shardJdbcTemplateListByCountry = shardJdbcTemplateListByCountry;
        this.shardJdbcTemplateListByIds = shardJdbcTemplateListByIds;
    }

    public JdbcTemplate selectWriteShardByCountry(String shardKey) {
        if(!shardJdbcTemplateListByCountry.containsKey(shardKey)) {
            throw new UnableToFindShardException(shardKey);
        }

        return shardJdbcTemplateListByCountry.get(shardKey).getWrite();
    }

    public JdbcTemplate selectReadShardByCountry(String shardKey) {
        if(!shardJdbcTemplateListByCountry.containsKey(shardKey)) {
            throw new UnableToFindShardException(shardKey);
        }

        return shardJdbcTemplateListByCountry.get(shardKey).getRead();
    }

    public JdbcTemplate selectWriteShardById(Integer id) {
        if(!shardJdbcTemplateListByIds.containsKey(id)) {
            throw new UnableToFindShardException(id);
        }

        return shardJdbcTemplateListByIds.get(id).getWrite();
    }

    public JdbcTemplate selectReadShardById(Integer id) {
        if(!shardJdbcTemplateListByIds.containsKey(id)) {
            throw new UnableToFindShardException(id);
        }

        return shardJdbcTemplateListByIds.get(id).getRead();
    }
}
