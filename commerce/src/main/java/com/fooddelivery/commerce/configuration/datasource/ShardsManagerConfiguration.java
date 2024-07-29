package com.fooddelivery.commerce.configuration.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ShardsManagerConfiguration {

    @Bean
    @Qualifier("shardsByCountry")
    public Map<String, ShardGroup> shardJdbcTemplatesByCountry(List<ShardGroup> shardGroups) {
        final Map<String , ShardGroup> jdbcTemplateList = new HashMap<>();

        for(ShardGroup shardGroup : shardGroups) {
            jdbcTemplateList.put(shardGroup.getCountry(), shardGroup);
        }

        return jdbcTemplateList;
    }

    @Bean
    @Qualifier("shardsByIds")
    public Map<Integer, ShardGroup> shardJdbcTemplatesByIds(List<ShardGroup> shardGroups) {
        Map<Integer , ShardGroup> jdbcTemplateList = new HashMap<>();

        for(ShardGroup shardGroup : shardGroups) {
            jdbcTemplateList.put(shardGroup.getIndex(), shardGroup);
        }

        return jdbcTemplateList;
    }

    @Bean
    @Qualifier("shardsTransactionManagers")
    public Map<Integer, PlatformTransactionManager> shardJdbcTemplateTransactionManagers(
            List<ShardGroup> shardGroups
    ) {
        final Map<Integer, PlatformTransactionManager> managers = new HashMap<>();

        for(ShardGroup shardGroup : shardGroups) {
            managers.put(shardGroup.getIndex(), shardGroup.getTransactionManager());
        }

        return managers;
    }

}

