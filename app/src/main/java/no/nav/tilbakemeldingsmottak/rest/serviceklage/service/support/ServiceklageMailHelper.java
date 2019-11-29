package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.exceptions.ServiceklageMailException;
import no.nav.tilbakemeldingsmottak.rest.common.epost.AbstractEmailService;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

@Component
@RequiredArgsConstructor
public class ServiceklageMailHelper {

    private final AbstractEmailService emailService;

    public void sendEmail(String fromAddress, String toAddress, String subject, String text, byte[] fysiskDokument) {
        try {
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(text);

            MimeMultipart content = new MimeMultipart();
            content.addBodyPart(textBodyPart);

            if (fysiskDokument != null) {
                DataSource dataSource = new ByteArrayDataSource(fysiskDokument, "application/pdf");
                MimeBodyPart pdfBodyPart = new MimeBodyPart();
                pdfBodyPart.setDataHandler(new DataHandler(dataSource));
                pdfBodyPart.setFileName("klage.pdf");
                content.addBodyPart(pdfBodyPart);
            }

            MimeMessage message = emailService.getEmailSender().createMimeMessage();
            message.setHeader("Content-Encoding", "UTF-8");
            message.setSender(new InternetAddress(fromAddress));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
            message.setSubject(subject);
            message.setContent(content);

            emailService.sendMail(message);
        } catch (MessagingException e) {
            throw new ServiceklageMailException("Kan ikke sende mail");
        }
    }

    public void sendEmail(String fromAddress, String toAddress, String subject, String text) {
        sendEmail(fromAddress, toAddress, subject, text, null);
    }

}
