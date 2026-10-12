package no.nav.tilbakemeldingsmottak.consumer.email

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "email", name = ["enabled"], havingValue = "false", matchIfMissing = true)
class LoggingEmailService : EmailService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun sendSimpleMessage(mottaker: String, subject: String, content: String) {
        logNotification()
    }

    override fun sendSimpleMessage(mottakere: List<String>, subject: String, content: String) {
        logNotification()
    }

    override fun sendMessageWithAttachments(
        mottaker: String,
        subject: String,
        content: String,
        attachment: ByteArray,
        attachmentName: String
    ) {
        logNotification()
    }

    override fun sendMessageWithAttachments(
        mottakere: List<String>,
        subject: String,
        content: String,
        attachment: ByteArray,
        attachmentName: String
    ) {
        logNotification()
    }

    private fun logNotification() {
        log.info("E-postvarsling er deaktivert. Ingen epost sendt.")
    }
}
