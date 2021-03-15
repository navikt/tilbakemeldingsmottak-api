package no.nav.tilbakemeldingsmottak.itest.config;

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import no.nav.tilbakemeldingsmottak.CoreConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@EnableAutoConfiguration
@EnableMockOAuth2Server
@Import({CoreConfig.class})
public class TestConfig {
}
