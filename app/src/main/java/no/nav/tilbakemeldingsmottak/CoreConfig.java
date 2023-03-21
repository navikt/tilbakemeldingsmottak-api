package no.nav.tilbakemeldingsmottak;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import no.nav.tilbakemeldingsmottak.metrics.DokTimedAspect;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */

@Configuration
@EnableAspectJAutoProxy
@EnableJwtTokenValidation(ignore={"org.springframework", "springfox.documentation.swagger.web.ApiResourceController"})
@EnableOAuth2Client(cacheEnabled = true)
@ConfigurationPropertiesScan
public class CoreConfig {

    @Bean
    public DokTimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new DokTimedAspect(meterRegistry);
    }

}
