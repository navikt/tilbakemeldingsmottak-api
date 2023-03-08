package no.nav.tilbakemeldingsmottak.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableJpaRepositories(basePackages = "no.nav.tilbakemeldingsmottak.repository")
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
        System.out.println("Environment username: " + env.getProperty("DATABASE_USERNAME"));

        System.out.println("datasource url: " + dataSourceProperties.getUrl());
        System.out.println("datasource username: " + dataSourceProperties.getUsername());
        System.out.println("datasource driver class name: " + dataSourceProperties.getDriverClassName());

         DataSource dataSource = DataSourceBuilder
                 .create()
                 .driverClassName(dataSourceProperties.getDriverClassName())
                 .url(dataSourceProperties.getUrl())
                 .username(dataSourceProperties.getUsername())
                 .password(dataSourceProperties.getPassword())
                 .build();

        return dataSource;
    }
}
