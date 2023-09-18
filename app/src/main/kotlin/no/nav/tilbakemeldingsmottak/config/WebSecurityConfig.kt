package no.nav.tilbakemeldingsmottak.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class WebSecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { csrf -> csrf.disable() }.build()
    }
}
