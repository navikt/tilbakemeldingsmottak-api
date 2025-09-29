package no.nav.tilbakemeldingsmottak.itest

import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.util.builders.BestillSamtaleRequestBuilder
import org.junit.jupiter.api.Test

internal class BestillingAvSamtaleIT : ApplicationTest() {

    private val URL_BESTILLING_AV_SAMTALE = "/rest/bestilling-av-samtale"

    @Test
    fun `happy path`() {
        // Given
        val request = BestillSamtaleRequestBuilder().build()

        // When / Then
        restTemplate!!.post()
            .uri(URL_BESTILLING_AV_SAMTALE)
            .headers { it.addAll(createHeaders()) }
            .bodyValue(request)
            .exchange()
            .expectStatus().is2xxSuccessful
    }
}
