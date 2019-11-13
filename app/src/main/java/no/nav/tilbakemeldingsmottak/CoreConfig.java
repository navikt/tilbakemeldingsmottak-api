package no.nav.tilbakemeldingsmottak;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.tilbakemeldingsmottak.integration.fasit.ServiceuserAlias;
import no.nav.tilbakemeldingsmottak.metrics.DokTimedAspect;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */

@ComponentScan(basePackages = "no.nav.tilbakemeldingsmottak")
@Configuration
@EnableAutoConfiguration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(ServiceuserAlias.class)
public class CoreConfig {

    @Bean
    public DokTimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new DokTimedAspect(meterRegistry);
    }

}
