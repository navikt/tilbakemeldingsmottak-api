package no.nav.tilbakemeldingsmottak.itest

import io.mockk.slot
import io.mockk.verify
import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.config.Constants
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels
import no.nav.tilbakemeldingsmottak.model.SendRosResponse
import no.nav.tilbakemeldingsmottak.util.builders.SendRosRequestBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import com.microsoft.graph.models.Message
import no.nav.tilbakemeldingsmottak.model.SendRosRequest
import org.junit.jupiter.api.Assertions.assertTrue


internal class RosIT : ApplicationTest() {
    private val URL_ROS = "/rest/ros"

    val tilbakemeldinger = "tilbakemeldinger"
    val userId = "01010096460"
    @Test
    fun happyPath() {
        // Given
        val request = SendRosRequestBuilder().build()
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, tilbakemeldinger, loggedIn = false))

        // When
        val response = restTemplate!!.exchange(URL_ROS, HttpMethod.POST, requestEntity, SendRosResponse::class.java)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1.0, metricsRegistery.get(MetricLabels.DOK_REQUEST + "_not_logged_in").counter().count())
    }

    @Test
    fun happyPathNavKontor() {
        // Given
        val request = SendRosRequestBuilder().withNavKontor().build()
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, tilbakemeldinger, loggedIn = false))

        // When
        val response = restTemplate!!.exchange(URL_ROS, HttpMethod.POST, requestEntity, SendRosResponse::class.java)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `validering feiler, meldingslengde for stor`() {
        // Given
        val request =
            SendRosRequestBuilder().withNavKontor().build(melding = "Dette er en for lang melding.".repeat(500))
        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, tilbakemeldinger, loggedIn = false))

        // When
        val response = restTemplate!!.exchange(URL_ROS, HttpMethod.POST, requestEntity, SendRosResponse::class.java)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `validering, html input blir vasket`() {
        // Given
        val request = SendRosRequestBuilder().withNavKontor()
            .build(melding = "Dette er en melding med html tagger.  <script>alert('Hallo, hvordan går det?');</script>")

        val requestEntity =
            HttpEntity(request, createHeaders(Constants.AZURE_ISSUER, tilbakemeldinger, loggedIn = false))

        // When
        val response = restTemplate!!.exchange(URL_ROS, HttpMethod.POST, requestEntity, SendRosResponse::class.java)
        val messageCapture = mutableListOf<Message>()
        verify(atLeast = 1) { aadMailClient.sendMailViaClient(capture(messageCapture)) }

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        val messageSent = messageCapture.last().body
        assertTrue(messageSent != null && messageSent.content != null)
        assertTrue(messageSent!!.content.contains("&lt;script&gt;alert('Hallo, hvordan g&aring;r det?');&lt;/script&gt;"))
    }

}
