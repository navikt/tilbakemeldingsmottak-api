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

    private static final String TILBAKEMELDINGSMOTTAK_DB_URL = "${tilbakemeldingsmottak_db_url}";
    private static final String APPLICATION_NAME = "tilbakemeldingsmottak";

    @Bean
    @Primary
    public DataSource userDataSource(@Value(TILBAKEMELDINGSMOTTAK_DB_URL) final String tilbakemeldingsmottakDbUrl) {
        return dataSource("user", tilbakemeldingsmottakDbUrl);
    }

    @SneakyThrows
    private HikariDataSource dataSource(String user, String tilbakemeldingsmottakDbUrl) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(tilbakemeldingsmottakDbUrl);
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        String mountPath = "postgresql/preprod-fss";
        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, dbRole(user));
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(@Value(TILBAKEMELDINGSMOTTAK_DB_URL) final String tilbakemeldingsmottakDbUrl) {
        return flyway -> Flyway.configure()
                .dataSource(dataSource("admin", tilbakemeldingsmottakDbUrl))
                .initSql(String.format("SET ROLE \"%s\"", dbRole("admin")))
                .load()
                .migrate();
    }

    private String dbRole(String role) {
        return String.join("-", APPLICATION_NAME, role);
    }
}
