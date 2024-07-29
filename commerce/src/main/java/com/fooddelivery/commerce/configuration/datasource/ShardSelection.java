package com.fooddelivery.commerce.configuration.datasource;

import org.springframework.jdbc.core.JdbcTemplate;

public interface ShardSelection {
    JdbcTemplate selectWriteShardByCountry(String shardKey);
    JdbcTemplate selectReadShardByCountry(String shardKey);
    JdbcTemplate selectWriteShardById(Integer id);
    JdbcTemplate selectReadShardById(Integer id);
}
