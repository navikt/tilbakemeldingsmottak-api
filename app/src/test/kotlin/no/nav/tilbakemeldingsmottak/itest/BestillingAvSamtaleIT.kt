package no.nav.tilbakemeldingsmottak.itest

import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleResponse
import no.nav.tilbakemeldingsmottak.util.builders.BestillSamtaleRequestBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

internal class BestillingAvSamtaleIT : ApplicationTest() {

    private val URL_BESTILLING_AV_SAMTALE = "/rest/bestilling-av-samtale"

    @Test
    fun `happy path`() {
        // Given
        val request = BestillSamtaleRequestBuilder().build()
        val requestEntity = HttpEntity<BestillSamtaleRequest>(request, createHeaders())

        // When
        val response: ResponseEntity<BestillSamtaleResponse> = restTemplate!!.exchange(
            URL_BESTILLING_AV_SAMTALE, HttpMethod.POST, requestEntity, BestillSamtaleResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
    }


    @Test
    fun `validation error, illegal telephone number`() {
        // Given
        val request = BestillSamtaleRequestBuilder().build(telefonnummer = "ABC-9999")
        val requestEntity = HttpEntity<BestillSamtaleRequest>(request, createHeaders())

        // When
        val response: ResponseEntity<BestillSamtaleResponse> = restTemplate!!.exchange(
            URL_BESTILLING_AV_SAMTALE, HttpMethod.POST, requestEntity, BestillSamtaleResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }


    @Test
    fun `validation error, periode ikke angitt`() {
        // Given
        val request = BestillSamtaleRequestBuilder().build(tidsrom = null)
        val requestEntity = HttpEntity<BestillSamtaleRequest>(request, createHeaders())

        // When
        val response: ResponseEntity<BestillSamtaleResponse> = restTemplate!!.exchange(
            URL_BESTILLING_AV_SAMTALE, HttpMethod.POST, requestEntity, BestillSamtaleResponse::class.java
        )

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }


}
