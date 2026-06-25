package no.nav.tilbakemeldingsmottak.local

import no.nav.tilbakemeldingsmottak.ApplicationTest
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(profiles = ["local", "itest", "localtest"], inheritProfiles = false)
internal class LocalSecurityDisabledIT : ApplicationTest() {
    @Test
    fun `local auth disabled mode allows protected endpoints without token`() {
        restTemplate!!
            .get()
            .uri("/test/security/claim")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java).isEqualTo("ok")
    }
}
