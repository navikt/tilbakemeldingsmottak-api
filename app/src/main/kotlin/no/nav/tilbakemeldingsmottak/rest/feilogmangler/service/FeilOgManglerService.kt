package no.nav.tilbakemeldingsmottak.rest.feilogmangler.service

import jakarta.inject.Inject
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FeilOgManglerService @Inject constructor(private val emailService: AzureEmailService) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${email_nav_support_address}")
    private val emailToAddress: String? = null

    fun meldFeilOgMangler(request: MeldFeilOgManglerRequest) {
        emailService.sendSimpleMessage(
            emailToAddress,
            "Feil/mangel på nav.no meldt via skjema på nav.no",
            createContent(request)
        )
        log.info("Melding om feil og mangler videresendt til $emailToAddress")
    }

    private fun createContent(request: MeldFeilOgManglerRequest): String {
        val content = HtmlContent()
        if (request.onskerKontakt == true) {
            content.addParagraph("Innsender ønsker å kontaktes på epost", request.epost)
        }
        content.addParagraph("Hva slags feil", request.feiltype?.value)
        content.addParagraph("Melding", request.melding)
        return content.contentString
    }
}
