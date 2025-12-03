package no.nav.tilbakemeldingsmottak.itest

import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerResponse
import no.nav.tilbakemeldingsmottak.util.builders.MeldFeilOgManglerRequestBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

internal class FeilOgManglerIT : ApplicationTest() {
    private val URL_FEIL_OG_MANGLER = "/rest/feil-og-mangler"

    @Test
    fun `happy path`() {
        // Given
        val request = MeldFeilOgManglerRequestBuilder().build()
        val requestEntity = HttpEntity(request, createHeaders())

        // When
        val response: ResponseEntity<MeldFeilOgManglerResponse> = restTemplate!!.exchange(
            URL_FEIL_OG_MANGLER, HttpMethod.POST, requestEntity, MeldFeilOgManglerResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
    }


    @Test
    fun `validation error, message too long`() {
        // Given
        val request = MeldFeilOgManglerRequestBuilder().build(
            onskerKontakt = true,
            melding = "Det er en feil på skjema.".repeat(500)
        )
        val requestEntity = HttpEntity(request, createHeaders())

        // When
        val response: ResponseEntity<MeldFeilOgManglerResponse> = restTemplate!!.exchange(
            URL_FEIL_OG_MANGLER, HttpMethod.POST, requestEntity, MeldFeilOgManglerResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }


}
