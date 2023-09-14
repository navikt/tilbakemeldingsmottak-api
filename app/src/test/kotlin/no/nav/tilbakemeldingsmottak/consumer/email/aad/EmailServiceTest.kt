package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.microsoft.graph.models.BodyType
import com.microsoft.graph.models.Message
import no.nav.tilbakemeldingsmottak.ApplicationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.*
import java.io.IOException

internal class EmailServiceTest : ApplicationTest() {

    @Mock
    lateinit var mailClient: AADMailClient

    @InjectMocks
    lateinit var azureEmailService: AzureEmailService

    @Captor
    lateinit var emailCaptor: ArgumentCaptor<Message>

    @Test
    fun `test send message`() {
        // Given
        val content = "This is a test message"
        val subject = "Test of messageService"
        val emailToAddress = "toMail@trygdeetaten.no"
        val emailToAddress2 = "extraReceiver@trygdeetaten.no"
        val to = listOf(emailToAddress, emailToAddress2)

        // When
        azureEmailService.sendSimpleMessage(to, subject, content)

        // Then
        Mockito.verify(mailClient).sendMailViaClient(emailCaptor.capture())
        val value = emailCaptor.value
        assertEquals(BodyType.HTML, value.body?.contentType)
        assertEquals(emailToAddress, value.toRecipients?.get(0)?.emailAddress?.address)
        assertEquals(emailToAddress2, value.toRecipients?.get(1)?.emailAddress?.address)
        assertEquals(subject, value.subject)
    }

    @Test
    fun `test send message with attachment`() {
        // Given
        val content = "This is a test message"
        val subject = "Test of messageService"
        val emailToAddress = "toMail@trygdeetaten.no"
        val attachment = readFileFromClasspath("/litenPdf.pdf")
        val attachmentName = "serviceklage.pdf"

        // When
        azureEmailService.sendMessageWithAttachments(emailToAddress, subject, content, attachment, attachmentName)

        // Then
        Mockito.verify(mailClient).sendMailViaClient(emailCaptor.capture())
        val value = emailCaptor.value
        assertEquals(BodyType.HTML, value.body?.contentType)
        assertEquals(emailToAddress, value.toRecipients?.get(0)?.emailAddress?.address)
        assertEquals(subject, value.subject)
        assertEquals(attachmentName, value.attachments?.currentPage?.get(0)?.name)
    }

    @Throws(IOException::class)
    private fun readFileFromClasspath(filename: String): ByteArray {
        val inputStream = EmailServiceTest::class.java.getResourceAsStream(filename)
        return inputStream?.readAllBytes() ?: throw IOException("File not found")
    }
}
