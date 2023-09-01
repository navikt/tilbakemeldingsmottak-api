package no.nav.tilbakemeldingsmottak;

import com.graphql_java_generator.client.GraphqlClientUtils;
import no.nav.tilbakemeldingsmottak.graphql.util.QueryExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class}, scanBasePackageClasses = {Application.class, GraphqlClientUtils.class, QueryExecutor.class})
@ConfigurationPropertiesScan
@EnableRetry(proxyTargetClass = true)
@EnableCaching
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
