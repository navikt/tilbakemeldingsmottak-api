package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.service

import jakarta.inject.Inject
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class BestillingAvSamtaleService @Inject constructor(private val emailService: AzureEmailService) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${email_samisk_kontakt_address}")
    private val emailToAddress: String? = null
    
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
        content.addParagraph("Fornavn", request.fornavn)
        content.addParagraph("Etternavn", request.etternavn)
        content.addParagraph("Telefonnummer", request.telefonnummer)
        content.addParagraph("Tidsrom", request.tidsrom!!.value)
        return content.contentString
    }

}
