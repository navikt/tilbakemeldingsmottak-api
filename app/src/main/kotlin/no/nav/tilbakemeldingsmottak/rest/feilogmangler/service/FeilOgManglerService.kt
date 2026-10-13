package no.nav.tilbakemeldingsmottak.rest.feilogmangler.service

import no.nav.tilbakemeldingsmottak.consumer.email.EmailService
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FeilOgManglerService(private val emailService: EmailService) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${email_nav_support_address}")
    private lateinit var emailToAddress: String

    fun meldFeilOgMangler(request: MeldFeilOgManglerRequest) {
        emailService.sendSimpleMessage(
            emailToAddress,
            "Feil/mangel på nav.no meldt via skjema på nav.no",
            createContent(request)
        )
        log.info("Varsling om feil og mangler behandlet")
    }

    private fun createContent(request: MeldFeilOgManglerRequest): String {
        val content = HtmlContent()
        if (request.onskerKontakt == true) {
            request.epost?.let { content.addParagraph("Innsender ønsker å kontaktes på epost", it) }
        }
        request.feiltype?.value?.let { content.addParagraph("Hva slags feil", it) }
        request.melding?.let { content.addParagraph("Melding", StringEscapeUtils.escapeHtml4(it)) }
        return content.contentString
    }
}
