package no.nav.tilbakemeldingsmottak

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry

@Configuration
@SpringBootApplication(
    exclude = [UserDetailsServiceAutoConfiguration::class]
)
@ConfigurationPropertiesScan
@EnableRetry(proxyTargetClass = true)
@EnableCaching
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

