package com.fooddelivery.commerce.configuration.datasource.shards;

import com.fooddelivery.commerce.configuration.datasource.ShardGroup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

// de = germany
@Configuration
public class DeShardConfiguration {

    public static int getIndex() {
        return 1;
    }

    public static String getCountry() {
        return "de";
    }

    @Bean
    @ConfigurationProperties("spring.datasource.de-read")
    public DataSourceProperties getDEReadDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.de-write")
    public DataSourceProperties getDEWriteDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Qualifier("shardDataSourceReadDE")
    public DataSource getDEReadDataSource() {
        return getDEReadDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Qualifier("shardDataSourceWriteDE")
    public DataSource getDEWriteDataSource() {
        return getDEWriteDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Qualifier("writeShardTransactionManagerDE")
    public PlatformTransactionManager getDEWriteTransactionManager(
            @Qualifier("shardDataSourceWriteDE")
            DataSource shardDataSourceDE
    ) {
        return new DataSourceTransactionManager(shardDataSourceDE);
    }

    @Bean
    @Qualifier("shardGroupDE")
    public ShardGroup getDEShardGroup(
            @Qualifier("shardDataSourceWriteDE")
            DataSource writeDataSource,
            @Qualifier("shardDataSourceReadDE")
            DataSource readDataSource,
            @Qualifier("writeShardTransactionManagerDE")
            PlatformTransactionManager writeTransactionManager
    ) {
        return new ShardGroup(
                writeDataSource,
                readDataSource,
                getIndex(),
                getCountry(),
                writeTransactionManager
        );
    }
}

