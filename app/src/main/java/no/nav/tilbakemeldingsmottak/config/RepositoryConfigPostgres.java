package no.nav.tilbakemeldingsmottak.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(DataSourceProperties.class)
@Profile("nais")
public class RepositoryConfigPostgres {
    private final DataSourceProperties dataSourceProperties;
    private final Environment env;

    public RepositoryConfigPostgres(DataSourceProperties dataSourceProperties, Environment env) {
        this.dataSourceProperties = dataSourceProperties;
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        System.out.println("Environment: " + env.getProperty("DATABASE_HOST"));
        System.out.println("Environment2: " + env.getProperty("NAIS_DATABASE_tilbakemeldingsmottak-api_tilbakemeldingsmottak-dev_HOST"));

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(dataSourceProperties.getUrl());
        dataSource.setUsername(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());
        System.out.println("datasource url: " + dataSource.getUrl());
        System.out.println("datasource username: " + dataSource.getUsername());

        return dataSource;
    }
}
