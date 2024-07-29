package com.fooddelivery.commerce.configuration.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.Map;

@Service
public class ShardTransactionManagerResolver  {

    private final Map<Integer, PlatformTransactionManager> transactionManagers;

    public ShardTransactionManagerResolver(
            @Qualifier("shardsTransactionManagers")
            Map<Integer, PlatformTransactionManager> shardJdbcTemplateTransactionManagers
    ) {
        this.transactionManagers = shardJdbcTemplateTransactionManagers;
    }

    public PlatformTransactionManager getTransactionalManagerByShardKey(Integer shardKey) {
        return transactionManagers.get(shardKey);
    }
}