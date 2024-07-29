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

// fr = france
@Configuration
public class FrShardConfiguration {
    public static int getIndex() {
        return 2;
    }

    public static String getCountry() {
        return "fr";
    }

    @Bean
    @ConfigurationProperties("spring.datasource.fr-read")
    public DataSourceProperties getFRReadDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.fr-write")
    public DataSourceProperties getFRWriteDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Qualifier("shardDataSourceReadFR")
    public DataSource getFRReadDataSource() {
        return getFRReadDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Qualifier("shardDataSourceWriteFR")
    public DataSource getFRWriteDataSource() {
        return getFRWriteDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Qualifier("writeShardTransactionManagerFR")
    public PlatformTransactionManager getFRWriteTransactionManager(
            @Qualifier("shardDataSourceWriteFR")
            DataSource shardDataSourceDE
    ) {
        return new DataSourceTransactionManager(shardDataSourceDE);
    }

    @Bean
    @Qualifier("shardGroupFR")
    public ShardGroup getFRShardGroup(
            @Qualifier("shardDataSourceWriteFR")
            DataSource writeDataSource,
            @Qualifier("shardDataSourceReadFR")
            DataSource readDataSource,
            @Qualifier("writeShardTransactionManagerFR")
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

