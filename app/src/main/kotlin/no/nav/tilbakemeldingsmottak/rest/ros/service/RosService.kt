package no.nav.tilbakemeldingsmottak.rest.ros.service

import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService
import no.nav.tilbakemeldingsmottak.model.SendRosRequest
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RosService(private val emailService: AzureEmailService) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${email_nav_support_address}")
    private val emailToAddress: String? = null

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
        if (SendRosRequest.HvemRoses.NAV_KONTOR == request.hvemRoses) {
            content.addParagraph("NAV-kontor", request.navKontor)
        }
        content.addParagraph("Melding", request.melding)
        return content.contentString
    }

}
