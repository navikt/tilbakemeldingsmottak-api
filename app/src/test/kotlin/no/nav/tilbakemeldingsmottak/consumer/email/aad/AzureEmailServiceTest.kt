package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.microsoft.graph.models.BodyType
import com.microsoft.graph.models.FileAttachment
import com.microsoft.graph.models.Message
import no.nav.tilbakemeldingsmottak.consumer.email.EmailService
import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.test.util.ReflectionTestUtils

class AzureEmailServiceTest {
    private val mailClient: AADMailClient = mock()
    private val service: EmailService = AzureEmailService(mailClient).also {
        ReflectionTestUtils.setField(it, "emailFromAddress", "sender@example.test")
    }

    @Test
    fun `all notification methods preserve recipients content and attachments`() {
        val recipient = "recipient@example.test"
        val recipients = listOf(recipient, "other@example.test")
        val subject = "Test subject"
        val content = "<p>Test content</p>"
        val attachment = "Test PDF".toByteArray()

        service.sendSimpleMessage(recipient, subject, content)
        service.sendSimpleMessage(recipients, subject, content)
        service.sendMessageWithAttachments(recipient, subject, content, attachment, "klage.pdf")
        service.sendMessageWithAttachments(recipients, subject, content, attachment, "klage.pdf")

        val captor = argumentCaptor<Message>()
        verify(mailClient, times(4)).sendMailViaClient(captor.capture())
        captor.allValues.forEachIndexed { index, message ->
            assertEquals(subject, message.subject)
            assertEquals(BodyType.Html, message.body?.contentType)
            assertEquals(content, message.body?.content)
            assertEquals("sender@example.test", message.from?.emailAddress?.address)
            assertEquals("sender@example.test", message.sender?.emailAddress?.address)
            assertEquals(
                if (index % 2 == 0) listOf(recipient) else recipients,
                message.toRecipients?.map { it.emailAddress?.address }
            )
            if (index < 2) {
                assertNull(message.attachments)
            } else {
                val file = assertInstanceOf(FileAttachment::class.java, message.attachments?.single())
                assertEquals("klage.pdf", file.name)
                assertEquals("application/pdf", file.contentType)
                assertArrayEquals(attachment, file.contentBytes)
            }
        }
    }

    @Test
    fun `mail client failures are propagated`() {
        doThrow(IllegalStateException("Mail delivery failed"))
            .`when`(mailClient).sendMailViaClient(any())

        assertThrows(SendEmailException::class.java) {
            service.sendSimpleMessage("recipient@example.test", "Test subject", "Test content")
        }
    }
}
