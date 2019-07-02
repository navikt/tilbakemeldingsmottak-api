package no.nav.tilbakemeldingsmottak.service.epost;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Profile("local")
@Component
public class DummyEmailServiceImpl extends AbstractEmailService {

    public DummyEmailServiceImpl(JavaMailSender emailSender) {
        super(emailSender);
    }

    @Override
    public void sendMail(MimeMessage message) throws MessagingException {

    }

}
