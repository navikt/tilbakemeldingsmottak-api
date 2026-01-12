package no.nav.tilbakemeldingsmottak

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication(
    exclude = [OAuth2ResourceServerAutoConfiguration::class]
)
@ConfigurationPropertiesScan
@EnableRetry(proxyTargetClass = true)
@EnableCaching
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

