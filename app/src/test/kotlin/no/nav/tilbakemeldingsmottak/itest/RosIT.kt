package no.nav.tilbakemeldingsmottak.itest

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
}
