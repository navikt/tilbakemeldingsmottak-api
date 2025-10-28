package no.nav.tilbakemeldingsmottak

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration

@TestConfiguration
class WebTestClientConfig {

    @Bean
    fun webTestClient(): WebTestClient {
        return WebTestClient.bindToServer()
            .responseTimeout(Duration.ofMinutes(2))
            .build()
    }
}
