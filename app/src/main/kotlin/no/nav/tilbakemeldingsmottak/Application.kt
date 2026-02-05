package no.nav.tilbakemeldingsmottak

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.resilience.annotation.EnableResilientMethods

@SpringBootApplication(
    exclude = [OAuth2ResourceServerAutoConfiguration::class]
)
@ConfigurationPropertiesScan
@EnableResilientMethods(proxyTargetClass = true)
@EnableCaching
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

