package no.nav.tilbakemeldingsmottak

import io.micrometer.core.instrument.MeterRegistry
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import no.nav.tilbakemeldingsmottak.metrics.DokTimedAspect
import no.nav.tilbakemeldingsmottak.metrics.MetricsUtils
import no.nav.tilbakemeldingsmottak.util.OidcUtils
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
@EnableJwtTokenValidation(ignore = ["org.springframework", "springfox.documentation.swagger.web.ApiResourceController"])
@EnableOAuth2Client(cacheEnabled = true)
@ConfigurationPropertiesScan
class CoreConfig {
    @Bean
    fun dokTimedAspect(meterRegistry: MeterRegistry, oidcUtils: OidcUtils, metricsUtils: MetricsUtils): DokTimedAspect {
        return DokTimedAspect(meterRegistry, oidcUtils, metricsUtils)
    }
}
