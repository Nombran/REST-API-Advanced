package com.epam.esm.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.epam.esm")
@PropertySource("classpath:application.properties")
public class DataSourceConfig {
    @Value("${dataSource.driver}")
    private String driverClassName;
    @Value("${dataSource.url}")
    private String databaseUrl;
    @Value("${dataSource.username}")
    private String databaseUsername;
    @Value("${dataSource.password}")
    private String databaseUserPassword;

    @Bean
    public DataSource mysqlDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(databaseUrl);
        hikariConfig.setUsername(databaseUsername);
        hikariConfig.setPassword(databaseUserPassword);
        hikariConfig.setDriverClassName(driverClassName);
        return new HikariDataSource(hikariConfig);
    }
}
