package no.nav.tilbakemeldingsmottak.service.epost;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Profile({"nais", "itest"})
@Component
@Slf4j
public class EmailServiceImpl extends AbstractEmailService{

    public EmailServiceImpl(JavaMailSender emailSender) {
        super(emailSender);
    }

    @Override
    public void sendMail(MimeMessage message) throws MessagingException {
        log.info("Sender mail til {} fra {}", message.getFrom(), message.getSender());
        emailSender.send(message);
    }
}
