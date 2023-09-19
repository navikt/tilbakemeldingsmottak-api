package no.nav.tilbakemeldingsmottak

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
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
object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        SpringApplication.run(Application::class.java, *args)
    }
}
