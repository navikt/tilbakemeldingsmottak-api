package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.*;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import com.microsoft.graph.requests.AttachmentCollectionResponse;
import no.nav.tilbakemeldingsmottak.consumer.email.EmailService;
import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.LinkedList;
import java.util.Objects;

@Component
public class AzureEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(AzureEmailService.class);

    private final AADMailClient mailClient;

    @Autowired
    public AzureEmailService(AADMailClient mailClient) {
        this.mailClient = mailClient;
    }

    @Value("${email_nav_support_address}")
    private String emailToAddress;

    @Value("${email_from_address}")
    private String emailFromAddress;

    private static final String NO_REPLY_ADDRESSE = "do-not-reply@nav.no";

    @Override
    public void sendSimpleMessage(String mottaker, String subject, String content) throws SendEmailException {
        Message message = createMessage(mottaker, subject, content);
        sendMessage(message);
    }

    @Override
    public void sendMessageWithAttachments(String mottaker, String subject, String content, byte[] attachment, String attachmentName) throws SendEmailException {
        Message message = createMessage(mottaker, subject, content);

        LinkedList<Attachment> attachmentsList = new LinkedList<Attachment>();
        FileAttachment attachments = new FileAttachment();
        attachments.name = attachmentName;
        attachments.contentType = "application/pdf"; // Assumes PDF
        attachments.contentBytes = attachment;
        attachmentsList.add(attachments);
        AttachmentCollectionResponse attachmentCollectionResponse = new AttachmentCollectionResponse();
        attachmentCollectionResponse.value = attachmentsList;
        message.attachments = new AttachmentCollectionPage(attachmentCollectionResponse, null);

        sendMessage(message);
    }

    private void sendMessage(Message message) {
        assert Objects.requireNonNull(message.from).emailAddress != null;
        assert Objects.requireNonNull(Objects.requireNonNull(message.toRecipients).get(0)).emailAddress != null;

        log.info("Send epost fra: " + message.from.emailAddress.address + " til: " + message.toRecipients.get(0).emailAddress.address);
        try {
            mailClient.sendMailViaClient(message);
            log.info("Epost sendt");
        } catch (Exception e) {
            log.error("Feil ved sending av epost fra: " + message.from.emailAddress.address + " til: " + message.toRecipients.get(0).emailAddress.address, e.getMessage());
            throw new SendEmailException(e.getMessage());
        }
    }


    private Message createMessage(String mottaker, String subject, String content) {
        Message message = new Message();
        message.subject = subject;
        ItemBody body = new ItemBody();
        body.contentType = BodyType.HTML;
        body.content = content;
        message.body = body;
        LinkedList<Recipient> toRecipientsList = new LinkedList<>();
        toRecipientsList.add(lagMottaker(mottaker == null ? emailToAddress : mottaker));
        message.toRecipients = toRecipientsList;
        message.from = lagMottaker(emailFromAddress);
        message.sender = lagMottaker(emailFromAddress);
        return message;
    }


    private Recipient lagMottaker(String mottakerAdresse) {
        Recipient recipient = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = mottakerAdresse;
        recipient.emailAddress = emailAddress;
        return recipient;
    }

}

