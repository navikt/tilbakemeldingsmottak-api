package no.nav.tilbakemeldingsmottak.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcDataSource;

@EnableTransactionManagement
@EnableConfigurationProperties(DataSourceProperties.class)
@Configuration
@Profile("local")
public class RepositoryConfig {

    private final Environment env;

    public RepositoryConfig(Environment env) {
        this.env = env;
    }

    @Bean
    @Primary
    DataSource dataSource(final DataSourceProperties dataSourceProperties) {
        System.out.println("Environment" + env.getProperty("DATABASE_URL"));

        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(dataSourceProperties.getUrl());
        dataSource.setUser(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());
        return dataSource;
    }
}


