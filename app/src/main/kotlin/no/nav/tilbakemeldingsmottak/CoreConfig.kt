package no.nav.tilbakemeldingsmottak

import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilbakemeldingsmottak.metrics.DokTimedAspect
import no.nav.tilbakemeldingsmottak.metrics.MetricsUtils
import no.nav.tilbakemeldingsmottak.util.OidcUtils
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
class CoreConfig {
    @Bean
    fun dokTimedAspect(meterRegistry: MeterRegistry, oidcUtils: OidcUtils, metricsUtils: MetricsUtils): DokTimedAspect {
        return DokTimedAspect(meterRegistry, oidcUtils, metricsUtils)
    }
}
