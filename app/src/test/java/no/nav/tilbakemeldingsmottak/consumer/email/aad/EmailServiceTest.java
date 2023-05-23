package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.Message;
import no.nav.tilbakemeldingsmottak.itest.ApplicationTest;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailServiceTest extends ApplicationTest {

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
        String emailToAddress2 = "extraReceiver@trygdeetaten.no";
        List to = Arrays.asList(emailToAddress, emailToAddress2);
        emailService.sendSimpleMessage(to, subject, content);

        Mockito.verify(mailClient).sendMailViaClient(emailCaptor.capture());
        Message value = emailCaptor.getValue();
        assertEquals(value.body.contentType, BodyType.HTML);
        assertEquals(emailToAddress, value.toRecipients.get(0).emailAddress.address);
        assertEquals(emailToAddress2, value.toRecipients.get(1).emailAddress.address);
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
