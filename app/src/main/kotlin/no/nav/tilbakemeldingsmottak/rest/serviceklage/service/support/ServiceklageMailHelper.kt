package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support

import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import org.springframework.stereotype.Component

@Component
class ServiceklageMailHelper(private val emailService: AzureEmailService) {
    fun sendEmail(
        fromAddress: String,
        toAddress: String,
        subject: String,
        text: String,
        fysiskDokument: ByteArray
    ) {
        try {
            val mottakere = toAddress.split(";")
            emailService.sendMessageWithAttachments(mottakere, subject, text, fysiskDokument, "klage.pdf")
        } catch (e: SendEmailException) {
            throw ServerErrorException("Kan ikke sende mail", e)
        }
    }

    fun sendEmail(fromAddress: String, toAddress: String, subject: String, text: String) {
        emailService.sendSimpleMessage(toAddress, subject, text)
    }
}
