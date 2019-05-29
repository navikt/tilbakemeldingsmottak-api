package no.nav.serviceklagemottak.config;

import oracle.net.ns.SQLnetDef;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Joakim Bjørnstad, Jbit AS
 */
@EnableTransactionManagement
@EnableConfigurationProperties(DataSourceProperties.class)
@Configuration
public class RepositoryConfig {
    @Bean
    @Primary
    DataSource dataSource(final DataSourceProperties dataSourceProperties) throws SQLException {
        PoolDataSource poolDataSource = PoolDataSourceFactory.getPoolDataSource();
        poolDataSource.setURL(dataSourceProperties.getUrl());
        poolDataSource.setUser(dataSourceProperties.getUsername());
        poolDataSource.setPassword(dataSourceProperties.getPassword());
        poolDataSource.setConnectionFactoryClassName(dataSourceProperties.getDriverClassName());

        Properties connProperties = new Properties();
        connProperties.setProperty(SQLnetDef.TCP_CONNTIMEOUT_STR, "3000");
        poolDataSource.setInitialPoolSize(5);
        poolDataSource.setMinPoolSize(2);
        poolDataSource.setMaxPoolSize(20);
        poolDataSource.setMaxConnectionReuseTime(300); // 5min
        poolDataSource.setMaxConnectionReuseCount(100);
        poolDataSource.setConnectionProperties(connProperties);
        return poolDataSource;
    }
}


