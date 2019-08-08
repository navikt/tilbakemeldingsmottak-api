package no.nav.tilbakemeldingsmottak.itest.config;

import no.nav.security.spring.oidc.test.TokenGeneratorConfiguration;
import no.nav.tilbakemeldingsmottak.CoreConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Sigurd Midttun, Visma Consulting.
 */
@Configuration
@EnableAutoConfiguration
@Import({TokenGeneratorConfiguration.class, CoreConfig.class})
public class TilknyttVedleggTestConfig {
}
