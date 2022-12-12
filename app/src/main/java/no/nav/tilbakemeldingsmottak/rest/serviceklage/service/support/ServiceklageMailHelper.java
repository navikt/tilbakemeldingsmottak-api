package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException;
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService;
import no.nav.tilbakemeldingsmottak.exceptions.ServiceklageMailException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            List<String> mottakere =  new ArrayList<>(Arrays.asList(toAddress.split(";")));
            emailService.sendMessageWithAttachments(mottakere, subject, text, fysiskDokument, "klage.pdf");
        } catch (SendEmailException e) {
            throw new ServiceklageMailException("Kan ikke sende mail");
        }
    }

    public void sendEmail(String fromAddress, String toAddress, String subject, String text) {
        emailService.sendSimpleMessage(toAddress, subject, text);
    }

}
