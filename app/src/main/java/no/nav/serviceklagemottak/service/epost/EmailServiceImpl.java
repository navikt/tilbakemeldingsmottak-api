package no.nav.serviceklagemottak.service.epost;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class EmailServiceImpl {

    @Inject
    private JavaMailSender emailSender;

    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("srvpam-kandidatsok@preprod.local");
        emailSender.send(message);
    }

}
