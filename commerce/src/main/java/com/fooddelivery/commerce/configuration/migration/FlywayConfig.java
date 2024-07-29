package com.fooddelivery.commerce.configuration.migration;

import com.fooddelivery.commerce.configuration.datasource.ShardGroup;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class FlywayConfig {
    private static final String location = "db/migration";

    @Bean
    public Flyway migration(List<ShardGroup> writeDataSources) {
        final List<Flyway> list = new ArrayList<>();

        for(ShardGroup dataSource : writeDataSources) {
            Flyway f = Flyway.configure()
                    .dataSource(dataSource.getWriteDataSource())
                    .locations(location)
                    .load();

            list.add(f);
            f.migrate();
        }

        return list.get(0);
    }
}