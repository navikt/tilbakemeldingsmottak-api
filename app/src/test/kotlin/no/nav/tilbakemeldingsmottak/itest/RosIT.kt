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
import org.springframework.test.web.servlet.post
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt

import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Value


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

    @Test
    fun `Kall mot sikret endepunkt skal returnere 401 Unauthorized uten token`() {
        mockMvc.post("/rest/ros")
            .andExpect { status { isUnauthorized() } }


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
        /*

                mockMvc.post("http://localhost:5490"+URL_ROS) {
                    with(jwt().jwt(mockJwt))
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("Ros sendt") }
                }
        */

        val response = restTemplate!!.post()
            .uri(URL_ROS)
            .accept(MediaType.APPLICATION_JSON)
            .headers { it.addAll(createHeaders(Constants.TOKENX_ISSUER, tilbakemeldinger, loggedIn = false)) }
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

        // When / Then
        val response = restTemplate!!.post()
            .uri(URL_ROS)
            .headers { it.addAll(createHeaders()) }
            .bodyValue(request)
            .exchange()
            .returnResult(SendRosResponse::class.java)

        // Then
        assertEquals(HttpStatus.OK, response.status)
        assertEquals(1.0, metricsRegistery.get(MetricLabels.DOK_REQUEST + "_not_logged_in").counter().count())
    }
}
