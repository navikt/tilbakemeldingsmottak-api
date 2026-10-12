package no.nav.tilbakemeldingsmottak.itest

import io.mockk.verify
import no.nav.tilbakemeldingsmottak.Application
import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.config.Constants.TOKENX_ISSUER
import no.nav.tilbakemeldingsmottak.consumer.email.EmailService
import no.nav.tilbakemeldingsmottak.consumer.email.LoggingEmailService
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.ServiceklageMailHelper
import no.nav.tilbakemeldingsmottak.util.builders.BestillSamtaleRequestBuilder
import no.nav.tilbakemeldingsmottak.util.builders.MeldFeilOgManglerRequestBuilder
import no.nav.tilbakemeldingsmottak.util.builders.SendRosRequestBuilder
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [Application::class],
    properties = [
        "spring.main.allow-bean-definition-overriding=true",
        "email.enabled=false",
        "email_from_address=",
        "email_serviceklage_address=",
        "email_nav_support_address=",
        "email_samisk_kontakt_address="
    ]
)
internal class PreprodNotificationsIT : ApplicationTest() {
    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var mailHelper: ServiceklageMailHelper

    @Value("\${auth.issuers.tokenx.issuer-uri}")
    lateinit var tokenxIssuer: String

    @Test
    fun `preprod accepts notifications with empty recipients without sending mail`() {
        assertInstanceOf(LoggingEmailService::class.java, emailService)
        `when`(tokenxJwtDecoder.decode(anyString())).thenReturn(createMockJwt(tokenxIssuer))

        listOf(
            "/rest/ros" to SendRosRequestBuilder().build(),
            "/rest/feil-og-mangler" to MeldFeilOgManglerRequestBuilder().build(),
            "/rest/bestilling-av-samtale" to BestillSamtaleRequestBuilder().build()
        ).forEach { (url, request) ->
            requireNotNull(restTemplate).post()
                .uri(url)
                .headers { it.addAll(createHeaders(TOKENX_ISSUER, "tilbakemeldinger")) }
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful
        }

        mailHelper.sendEmail("", "", "Test notification", "Test content")
        mailHelper.sendEmail("", "", "Test notification", "Test content", "Test PDF".toByteArray())
        mailHelper.sendEmail("", "caseworker@example.test", "Test receipt", "Test content", byteArrayOf())

        verify(exactly = 0) { aadMailClient.sendMailViaClient(any()) }
    }
}
