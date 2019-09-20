package no.nav.tilbakemeldingsmottak.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@EnableConfigurationProperties(DataSourceProperties.class)
@Configuration
@EnableJpaRepositories(basePackages = "no.nav.tilbakemeldingsmottak")
@Profile("nais")
public class DatabaseConfig {

    private static final String SERVICEKLAGE_DB_URL = "${serviceklage_db_url}";
    private static final String APPLICATION_NAME = "tilbakemeldingsmottak";

    @Bean
    @Primary
    public DataSource userDataSource(@Value(SERVICEKLAGE_DB_URL) final String serviceklageDbUrl) {
        return dataSource("user", serviceklageDbUrl);
    }

    @SneakyThrows
    private HikariDataSource dataSource(String user, String serviceklageDbUrl) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(serviceklageDbUrl);
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        String mountPath = "postgresql/preprod-fss";
        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, dbRole(user));
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(@Value(SERVICEKLAGE_DB_URL) final String serviceklageDbUrl) {
        return flyway -> Flyway.configure()
                .dataSource(dataSource("admin", serviceklageDbUrl))
                .initSql(String.format("SET ROLE \"%s\"", dbRole("admin")))
                .load()
                .migrate();
    }

    private String dbRole(String role) {
        return String.join("-", APPLICATION_NAME, role);
    }
}
