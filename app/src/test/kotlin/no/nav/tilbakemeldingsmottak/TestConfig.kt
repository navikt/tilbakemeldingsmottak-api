package no.nav.tilbakemeldingsmottak

import no.nav.tilbakemeldingsmottak.consumer.email.aad.AADMailClient
import org.mockito.kotlin.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration(proxyBeanMethods = false)
class TestConfig {

    @Bean
    @Primary
    fun aadMailClient(): AADMailClient = mock()
}
