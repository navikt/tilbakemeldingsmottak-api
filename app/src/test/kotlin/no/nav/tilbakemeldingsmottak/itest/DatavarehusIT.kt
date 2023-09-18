package no.nav.tilbakemeldingsmottak.itest

import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.model.DatavarehusServiceklage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

internal class DatavarehusIT : ApplicationTest() {

    private val URL_DATAVAREHUS = "/rest/datavarehus/serviceklage"

    @Test
    fun `happy path`() {
        // Given
        val requestEntity = HttpEntity<Any?>(null, createHeaders(AZURE_ISSUER, "12345", "datavarehus"))

        // When
        val response: ResponseEntity<Array<DatavarehusServiceklage>> = restTemplate!!.exchange(
            URL_DATAVAREHUS, HttpMethod.GET, requestEntity, Array<DatavarehusServiceklage>::class.java
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
