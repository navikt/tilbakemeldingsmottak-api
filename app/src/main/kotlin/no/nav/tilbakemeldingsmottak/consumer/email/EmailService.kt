package no.nav.tilbakemeldingsmottak.consumer.email

interface EmailService {
    fun sendSimpleMessage(mottaker: String, subject: String, content: String)

    fun sendSimpleMessage(mottakere: List<String>, subject: String, content: String)

    fun sendMessageWithAttachments(
        mottaker: String,
        subject: String,
        content: String,
        attachment: ByteArray,
        attachmentName: String
    )

    fun sendMessageWithAttachments(
        mottakere: List<String>,
        subject: String,
        content: String,
        attachment: ByteArray,
        attachmentName: String
    )
}