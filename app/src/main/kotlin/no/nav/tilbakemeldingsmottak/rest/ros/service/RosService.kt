package no.nav.tilbakemeldingsmottak.rest.ros.service

import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService
import no.nav.tilbakemeldingsmottak.model.SendRosRequest
import no.nav.tilbakemeldingsmottak.model.SendRosRequestHvemRoses
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RosService(private val emailService: AzureEmailService) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${email_nav_support_address}")
    private lateinit var emailToAddress: String

    fun sendRos(request: SendRosRequest) {
        emailService.sendSimpleMessage(
            emailToAddress,
            "Ros til NAV sendt inn via skjema på nav.no",
            createContent(request)
        )
        log.info("Ros til NAV videresendt til $emailToAddress")
    }

    private fun createContent(request: SendRosRequest): String {
        val content = HtmlContent()
        content.addParagraph("Hvem roses", request.hvemRoses.toString())
        if (SendRosRequestHvemRoses.NAV_KONTOR == request.hvemRoses) {
            request.navKontor?.let { content.addParagraph("NAV-kontor", it) }
        }
        request.melding?.let { content.addParagraph("Melding", htmlVaskInput(it)) }
        return content.contentString
    }

    fun htmlVaskInput(input: String): String {
        return StringEscapeUtils.escapeHtml4(input)
    }
}
