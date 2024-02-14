package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.microsoft.graph.models.*
import com.microsoft.graph.requests.AttachmentCollectionPage
import com.microsoft.graph.requests.AttachmentCollectionResponse
import no.nav.tilbakemeldingsmottak.consumer.email.EmailService
import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class AzureEmailService(private val mailClient: AADMailClient) : EmailService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${email_from_address}")
    private lateinit var emailFromAddress: String
    
    override fun sendSimpleMessage(mottaker: String, subject: String, content: String) {
        val mottakere = listOf(mottaker)
        sendSimpleMessage(mottakere, subject, content)
    }

    @Metrics(
        value = MetricLabels.DOK_CONSUMER,
        extraTags = [MetricLabels.PROCESS_CODE, "sendEpost"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    override fun sendSimpleMessage(mottakere: List<String>, subject: String, content: String) {
        val message = createMessage(mottakere, subject, content)
        sendMessage(message)
    }

    override fun sendMessageWithAttachments(
        mottaker: String,
        subject: String,
        content: String,
        attachment: ByteArray,
        attachmentName: String
    ) {
        val mottakere = listOf(mottaker)
        sendMessageWithAttachments(mottakere, subject, content, attachment, attachmentName)
    }

    @Metrics(
        value = MetricLabels.DOK_CONSUMER,
        extraTags = [MetricLabels.PROCESS_CODE, "sendEpost"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    override fun sendMessageWithAttachments(
        mottakere: List<String>,
        subject: String,
        content: String,
        attachment: ByteArray,
        attachmentName: String
    ) {
        val message = createMessage(mottakere, subject, content)
        log.info("Skal sende epost med vedlegg")
        val attachmentsList = LinkedList<Attachment>()
        val attachments = FileAttachment()
        attachments.name = attachmentName
        attachments.contentType = "application/pdf" // Assumes PDF
        attachments.contentBytes = attachment
        attachments.oDataType = "#microsoft.graph.fileAttachment"
        attachmentsList.add(attachments)
        val attachmentCollectionResponse = AttachmentCollectionResponse()
        attachmentCollectionResponse.value = attachmentsList
        message.attachments = AttachmentCollectionPage(attachmentsList, null)
        sendMessage(message)
    }

    private fun sendMessage(message: Message) {
        assert(Objects.requireNonNull(message.from)?.emailAddress != null)
        assert(Objects.requireNonNull(Objects.requireNonNull(message.toRecipients)[0]).emailAddress != null)
        assert(message.body != null)
        log.info("Send epost fra: " + message.from?.emailAddress?.address + " til: " + message.toRecipients?.get(0)?.emailAddress?.address + " body størrelse: " + message.body?.content?.length)
        try {
            mailClient.sendMailViaClient(message)
        } catch (e: Exception) {
            throw SendEmailException(e.message)
        }
    }

    private fun createMessage(mottakere: List<String>, subject: String, content: String): Message {
        val message = Message()
        message.subject = subject
        val body = ItemBody()
        body.contentType = BodyType.HTML
        body.content = content
        message.body = body
        val toRecipientsList = LinkedList<Recipient>()
        for (mottaker in mottakere) {
            toRecipientsList.add(lagMottaker(mottaker.trim { it <= ' ' }))
        }
        message.toRecipients = toRecipientsList
        message.from = lagMottaker(emailFromAddress)
        message.sender = lagMottaker(emailFromAddress)
        return message
    }

    private fun lagMottaker(mottakerAdresse: String?): Recipient {
        val recipient = Recipient()
        val emailAddress = EmailAddress()
        emailAddress.address = mottakerAdresse
        recipient.emailAddress = emailAddress
        return recipient
    }

}
