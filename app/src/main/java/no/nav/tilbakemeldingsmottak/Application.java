package no.nav.tilbakemeldingsmottak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@ConfigurationPropertiesScan
@EnableCaching
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
