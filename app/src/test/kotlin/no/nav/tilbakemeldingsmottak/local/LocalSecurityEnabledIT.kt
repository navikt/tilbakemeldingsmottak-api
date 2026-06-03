package no.nav.tilbakemeldingsmottak.local

import no.nav.tilbakemeldingsmottak.ApplicationTest
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(profiles = ["local", "itest"], inheritProfiles = false)
internal class LocalSecurityEnabledIT : ApplicationTest() {
    @Test
    fun `local profile still requires authentication when auth is enabled`() {
        restTemplate!!
            .get()
            .uri("/test/security/claim")
            .exchange()
            .expectStatus().isUnauthorized
    }
}
