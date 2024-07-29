package com.fooddelivery.commerce.configuration.datasource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Getter
@Setter
public class ShardGroup {
    public enum Type {
        read,
        write
    }

    private DataSource writeDataSource;
    private DataSource readDataSource;
    private JdbcTemplate write;
    private JdbcTemplate read;
    private int index;
    private String country;
    private PlatformTransactionManager transactionManager;


    public ShardGroup(
            DataSource writeDataSource,
            DataSource readDataSource,
            int index,
            String country,
            PlatformTransactionManager transactionManager
    ) {
        this.writeDataSource = writeDataSource;
        this.readDataSource = readDataSource;
        this.write = new JdbcTemplate(writeDataSource);
        this.read = new JdbcTemplate(readDataSource);
        this.index = index;
        this.country = country;
        this.transactionManager = transactionManager;
    }
}
