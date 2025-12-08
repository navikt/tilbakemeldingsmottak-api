package no.nav.tilbakemeldingsmottak.itest

import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.config.Constants
import no.nav.tilbakemeldingsmottak.util.builders.BestillSamtaleRequestBuilder
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Value

internal class BestillingAvSamtaleIT : ApplicationTest() {

    private val URL_BESTILLING_AV_SAMTALE = "/rest/bestilling-av-samtale"

    @Value("\${auth.issuers.tokenx.issuer-uri}")
    lateinit var tokenxIssuer: String

    val tilbakemeldinger = "tilbakemeldinger"

    @Test
    fun `happy path`() {
        // Given
        val request = BestillSamtaleRequestBuilder().build()
        val mockJwt = createMockJwt(tokenxIssuer)

        `when`(tokenxJwtDecoder.decode(anyString())).thenReturn(mockJwt)

        // When / Then
        restTemplate!!.post()
            .uri(URL_BESTILLING_AV_SAMTALE)
            .headers { it.addAll(createHeaders(Constants.TOKENX_ISSUER, tilbakemeldinger)) }
            .bodyValue(request)
            .exchange()
            .expectStatus().is2xxSuccessful
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
