package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.*;
import com.microsoft.graph.models.BodyType;
import no.nav.tilbakemeldingsmottak.consumer.email.EmailService;
import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

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

        log.info("Send epost fra: " + emailFromAddress + " til: " + mottaker);
        try {
            mailClient.sendMailViaClient(message);
        } catch (Exception e) {
            throw new SendEmailException(e.getMessage());
        }
    }

    private Recipient lagMottaker(String mottakerAdresse) {
        Recipient recipient = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = mottakerAdresse;
        recipient.emailAddress = emailAddress;
        return recipient;
    }

}

