package no.nav.tilbakemeldingsmottak.itest

import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.config.Constants
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels
import no.nav.tilbakemeldingsmottak.model.SendRosResponse
import no.nav.tilbakemeldingsmottak.model.SendRosRequest
import no.nav.tilbakemeldingsmottak.util.builders.SendRosRequestBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import tools.jackson.databind.ObjectMapper
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Value

//@WireMockTest
internal class RosIT : ApplicationTest() {
    private val URL_ROS = "/rest/ros"

    val tilbakemeldinger = "tilbakemeldinger"
    val userId = "01010096460"

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Value("\${auth.issuers.azuread.issuer-uri}")
    lateinit var azureIssuer: String

    @Value("\${auth.issuers.tokenx.issuer-uri}")
    lateinit var tokenxIssuer: String


    /*

        @AfterEach
        fun tearDown() {
            wm.resetAll()
        }
    */


    @Test
    fun `Kall mot sikret endepunkt skal returnere 401 Unauthorized uten token`() {
        restTemplate!!.post()
            .uri("/rest/ros")
            .exchange()
            .expectStatus().is4xxClientError
    }


    // Sjekk at Jackson config funker
    @Test
    fun checkJackson() {
        val valueOrig = SendRosRequestBuilder().build()
        val valueJson = objectMapper.writeValueAsString(valueOrig)
        val valueObj = objectMapper.readValue(valueJson, SendRosRequest::class.java)
        assertEquals(valueOrig, valueObj)
    }

    @Test
    fun happyPath() {
        // Given
        val request = SendRosRequestBuilder().build()
        //val requestJson = objectMapper.writeValueAsString(request)
        // When
        val mockJwt = createMockJwt(tokenxIssuer)

        `when`(azureJwtDecoder.decode(anyString())).thenReturn(mockJwt)
        `when`(tokenxJwtDecoder.decode(anyString())).thenReturn(mockJwt)

        val response = restTemplate!!.post()
            .uri(URL_ROS)
            .accept(MediaType.APPLICATION_JSON)
            .headers { it.addAll(createHeaders(Constants.AZURE_ISSUER, tilbakemeldinger, loggedIn = false)) }
            //.bodyValue(request)
            .bodyValue(request)
            .exchange()
            .returnResult(SendRosResponse::class.java)

        // Then
        assertEquals(HttpStatus.OK, response.status)
        assertEquals(1.0, metricsRegistery.get(MetricLabels.DOK_REQUEST + "_not_logged_in").counter().count())
    }

    @Test
    fun happyPathNavKontor() {
        // Given
        val request = SendRosRequestBuilder().withNavKontor().build()
        val mockJwt = createMockJwt(tokenxIssuer)

        `when`(azureJwtDecoder.decode(anyString())).thenReturn(mockJwt)

        val initialCount = metricsRegistery.get(MetricLabels.DOK_REQUEST + "_not_logged_in").counter().count()
        // When / Then
        val response = restTemplate!!.post()
            .uri(URL_ROS)
            .headers { it.addAll(createHeaders()) }
            .bodyValue(request)
            .exchange()
            .returnResult(SendRosResponse::class.java)

        // Then
        assertEquals(HttpStatus.OK, response.status)
        assertEquals(
            1.0 + initialCount,
            metricsRegistery.get(MetricLabels.DOK_REQUEST + "_not_logged_in").counter().count()
        )
    }
}
