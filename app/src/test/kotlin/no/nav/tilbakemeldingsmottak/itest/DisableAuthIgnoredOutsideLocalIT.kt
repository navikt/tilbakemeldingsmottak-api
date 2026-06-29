package no.nav.tilbakemeldingsmottak.itest

import no.nav.tilbakemeldingsmottak.ApplicationTest
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(profiles = ["itest", "disableauthtest"], inheritProfiles = false)
internal class DisableAuthIgnoredOutsideLocalIT : ApplicationTest() {
    @Test
    fun `disable auth property is ignored outside local profile`() {
        restTemplate!!
            .get()
            .uri("/test/security/claim")
            .exchange()
            .expectStatus().isUnauthorized
    }
}
