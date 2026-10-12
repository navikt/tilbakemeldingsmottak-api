package no.nav.tilbakemeldingsmottak.consumer.email

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension

@ExtendWith(OutputCaptureExtension::class)
class LoggingEmailServiceTest {
    @Test
    fun `all notification methods log without exposing message data`(output: CapturedOutput) {
        val service: EmailService = LoggingEmailService()
        val recipient = "recipient@example.test"
        val subject = "Test subject"
        val content = "Test message content"
        val attachment = "Test attachment content".toByteArray()
        val attachmentName = "test-attachment.pdf"

        service.sendSimpleMessage(recipient, subject, content)
        service.sendSimpleMessage(listOf(recipient), subject, content)
        service.sendMessageWithAttachments(recipient, subject, content, attachment, attachmentName)
        service.sendMessageWithAttachments(listOf(recipient), subject, content, attachment, attachmentName)

        val notificationLines = output.all.lineSequence()
            .filter { it.contains("E-postvarsling er deaktivert. Ingen epost sendt.") }
            .toList()
        assertEquals(4, notificationLines.size)
        notificationLines.forEach { assertTrue(it.contains("INFO")) }
        listOf(recipient, subject, content, String(attachment), attachmentName).forEach {
            assertFalse(output.all.contains(it))
        }
    }
}
