package no.nav.tilbakemeldingsmottak.itest

import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.util.builders.MeldFeilOgManglerRequestBuilder
import org.junit.jupiter.api.Test

internal class FeilOgManglerIT : ApplicationTest() {
    private val URL_FEIL_OG_MANGLER = "/rest/feil-og-mangler"

    @Test
    fun `happy path`() {
        // Given
        val request = MeldFeilOgManglerRequestBuilder().build()

        // When / Then
        restTemplate!!.post()
            .uri(URL_FEIL_OG_MANGLER)
            .headers { it.addAll(createHeaders()) }
            .bodyValue(request)
            .exchange()
            .expectStatus().is2xxSuccessful

    }


}
