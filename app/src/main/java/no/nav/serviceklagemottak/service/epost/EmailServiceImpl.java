package no.nav.serviceklagemottak.service.epost;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class EmailServiceImpl {

    private final JavaMailSender emailSender;

    @Inject
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("srvserviceklagemot@preprod.local");
        emailSender.send(message);
    }

}
