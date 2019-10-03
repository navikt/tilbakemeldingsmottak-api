package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.BestillSamtaleRequest;
import no.nav.tilbakemeldingsmottak.rest.common.epost.AbstractEmailService;
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class BestillingAvSamtaleService {

    private AbstractEmailService emailService;

    @Value("${email_to_address}")
    private String emailToAddress;
    @Value("${email_from_address}")
    private String emailFromAddress;

    @Inject
    public BestillingAvSamtaleService(AbstractEmailService emailService) {
        this.emailService = emailService;
    }

    public void bestillSamtale(BestillSamtaleRequest request) throws MessagingException {
        sendEmail(request);
        log.info("Ros sendt");
    }

    private void sendEmail(BestillSamtaleRequest request) throws MessagingException {
        MimeMessage message = emailService.getEmailSender().createMimeMessage();
        message.setHeader("Content-Encoding", "UTF-8");
        message.setContent(createContent(request), "text/html; charset=UTF-8");
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(emailToAddress);
        helper.setFrom(emailFromAddress);
        helper.setSubject("Bestilling av samtale");
        emailService.sendMail(message);
    }

    private String createContent(BestillSamtaleRequest request) {
        HtmlContent content = new HtmlContent();

        content.addParagraph("Fornavn", request.getFornavn());
        content.addParagraph("Etternavn", request.getEtternavn());
        content.addParagraph("Telefonnummer", request.getTelefonnummer());
        content.addParagraph("Tidsrom", request.getTidsrom().text);

        return content.getContentString();
    }

}
