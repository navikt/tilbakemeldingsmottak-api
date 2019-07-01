package no.nav.tilbakemeldingsmottak.service.epost;

import lombok.Getter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailServiceImpl {

    @Getter
    private final JavaMailSender emailSender;

    @Inject
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendMail(MimeMessage message) throws MessagingException {
        emailSender.send(message);
    }
}
