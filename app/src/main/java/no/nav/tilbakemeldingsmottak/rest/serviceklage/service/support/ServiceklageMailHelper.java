package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException;
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService;
import no.nav.tilbakemeldingsmottak.exceptions.ServiceklageMailException;
import no.nav.tilbakemeldingsmottak.rest.common.epost.AbstractEmailService;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.inject.Inject;
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

    private AzureEmailService emailService;

    @Inject
    public ServiceklageMailHelper(AzureEmailService emailService) {
        this.emailService = emailService;
    }

    public void sendEmail(String fromAddress, String toAddress, String subject, String text, byte[] fysiskDokument) {
        try {
            emailService.sendMessageWithAttachments(toAddress, subject, text, fysiskDokument, "klage.pdf");
        } catch (SendEmailException e) {
            throw new ServiceklageMailException("Kan ikke sende mail");
        }
    }

    public void sendEmail(String fromAddress, String toAddress, String subject, String text) {
        emailService.sendSimpleMessage(toAddress, subject, text);
    }

}
