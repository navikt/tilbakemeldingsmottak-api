package no.nav.tilbakemeldingsmottak.itest

import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.config.Constants
import no.nav.tilbakemeldingsmottak.util.builders.MeldFeilOgManglerRequestBuilder
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Value

internal class FeilOgManglerIT : ApplicationTest() {
    private val URL_FEIL_OG_MANGLER = "/rest/feil-og-mangler"

    @Value("\${auth.issuers.tokenx.issuer-uri}")
    lateinit var tokenxIssuer: String

    val tilbakemeldinger = "tilbakemeldinger"

    @Test
    fun `happy path`() {
        // Given
        val request = MeldFeilOgManglerRequestBuilder().build()
        val mockJwt = createMockJwt(tokenxIssuer)

        `when`(tokenxJwtDecoder.decode(anyString())).thenReturn(mockJwt)

        // When / Then
        restTemplate!!.post()
            .uri(URL_FEIL_OG_MANGLER)
            .headers { it.addAll(createHeaders(Constants.TOKENX_ISSUER, tilbakemeldinger)) }
            .bodyValue(request)
            .exchange()
            .expectStatus().is2xxSuccessful

    }


}
