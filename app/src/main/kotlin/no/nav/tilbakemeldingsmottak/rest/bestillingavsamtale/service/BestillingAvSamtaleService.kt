package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.service

import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class BestillingAvSamtaleService(private val emailService: AzureEmailService) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${email_samisk_kontakt_address}")
    private lateinit var emailToAddress: String

    fun bestillSamtale(request: BestillSamtaleRequest) {
        emailService.sendSimpleMessage(
            emailToAddress,
            "Bestilling av samtale mottatt via skjema på nav.no",
            createContent(request)
        )
        log.info("Bestilling av samtale videresendt til $emailToAddress")
    }

    private fun createContent(request: BestillSamtaleRequest): String {
        val content = HtmlContent()
        request.fornavn?.let { content.addParagraph("Fornavn", it) }
        request.etternavn?.let { content.addParagraph("Etternavn", it) }
        request.telefonnummer?.let { content.addParagraph("Telefonnummer", it) }
        request.tidsrom?.value?.let { content.addParagraph("Tidsrom", it) }
        return content.contentString
    }

}
