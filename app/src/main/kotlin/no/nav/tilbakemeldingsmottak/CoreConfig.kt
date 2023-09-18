package no.nav.tilbakemeldingsmottak

import graphql.scalars.ExtendedScalars
import graphql.schema.idl.RuntimeWiring
import io.micrometer.core.instrument.MeterRegistry
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import no.nav.tilbakemeldingsmottak.metrics.DokTimedAspect
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
@EnableAspectJAutoProxy
@EnableJwtTokenValidation(ignore = ["org.springframework", "springfox.documentation.swagger.web.ApiResourceController"])
@EnableOAuth2Client(cacheEnabled = true)
@ConfigurationPropertiesScan
class CoreConfig {
    @Bean
    fun timedAspect(meterRegistry: MeterRegistry?): DokTimedAspect {
        return DokTimedAspect(meterRegistry!!)
    }

    @Bean
    fun runtimeWiringConfigurer(): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { wiringBuilder: RuntimeWiring.Builder ->
            wiringBuilder.scalar(ExtendedScalars.DateTime).scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.GraphQLLong)
        }
    }
}
