package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    AADMailClient mailClient;

    @InjectMocks
    AzureEmailService emailService;

    @Captor
    ArgumentCaptor<Message> emailCaptor;

    @Test
    public void testSendMessage() throws Exception {
        String content = "This is a test message";
        String subject = "Test of messageService";
        String emailToAddress = "toMail@trygdeetaten.no";

        emailService.sendSimpleMessage(emailToAddress, subject, content);

        Mockito.verify(mailClient).sendMailViaClient(emailCaptor.capture());
        Message value = emailCaptor.getValue();
        assertEquals(value.body.contentType, BodyType.HTML);
        assertEquals(value.toRecipients.get(0).emailAddress.address, emailToAddress);
        assertEquals(value.subject, subject);

    }

    @Test
    public void testSendMessageWithAttachment() throws Exception {
        String content = "This is a test message";
        String subject = "Test of messageService";
        String emailToAddress = "toMail@trygdeetaten.no";
        byte[] attachment = readFileFromClasspath("/litenPdf.pdf");
        String attachmentName = "serviceklage.pdf";

        emailService.sendMessageWithAttachments(emailToAddress, subject, content, attachment, attachmentName);

        Mockito.verify(mailClient).sendMailViaClient(emailCaptor.capture());
        Message value = emailCaptor.getValue();
        assertEquals(value.body.contentType, BodyType.HTML);
        assertEquals(value.toRecipients.get(0).emailAddress.address, emailToAddress);
        assertEquals(value.subject, subject);
        assertEquals(value.attachments.getCurrentPage().get(0).name, attachmentName);

    }

    private byte[] readFileFromClasspath(String filename) throws IOException {
        InputStream inputStream = EmailServiceTest.class.getResourceAsStream(filename);
        return inputStream.readAllBytes();
    }

}
