package no.nav.tilbakemeldingsmottak.db;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;

@Configuration
@Profile({"local"})
public class StartDb {
    
    @Bean
    public DataSource embeddedPostgres() throws IOException {
        EmbeddedPostgres embeddedPostgres = EmbeddedPostgres.builder().start();
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(embeddedPostgres.getJdbcUrl("postgres"));
        dataSource.setUser("postgres");
        return dataSource;
    }
}
