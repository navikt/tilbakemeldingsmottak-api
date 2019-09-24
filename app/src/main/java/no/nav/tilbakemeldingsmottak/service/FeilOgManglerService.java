package no.nav.tilbakemeldingsmottak.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.api.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.service.epost.AbstractEmailService;
import no.nav.tilbakemeldingsmottak.service.epost.HtmlContent;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class FeilOgManglerService {

    private AbstractEmailService emailService;
    private static final String EMAIL_TO_ADDRESS = "${email_to_address}";
    private static final String EMAIL_FROM_ADDRESS = "${email_from_address}";

    @Inject
    public FeilOgManglerService(AbstractEmailService emailService) {
        this.emailService = emailService;
    }

    public void meldFeilOgMangler(MeldFeilOgManglerRequest request) throws MessagingException {
        sendEmail(request);
        log.info("Feil/mangel meldt");
    }

    private void sendEmail(MeldFeilOgManglerRequest request) throws MessagingException {
        MimeMessage message = emailService.getEmailSender().createMimeMessage();
        message.setHeader("Content-Encoding", "UTF-8");
        message.setContent(createContent(request), "text/html; charset=UTF-8");
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(EMAIL_TO_ADDRESS);
        helper.setFrom(EMAIL_FROM_ADDRESS);
        helper.setSubject("Feilmelding mottatt");
        emailService.sendMail(message);
    }

    private String createContent(MeldFeilOgManglerRequest request) {
        HtmlContent content = new HtmlContent();

        content.addParagraph("Navn", request.getNavn());
        content.addParagraph("Telefonnummer", request.getTelefonnummer());
        content.addParagraph("Hva slags feil", request.getFeiltype().text);
        content.addParagraph("Melding", request.getMelding());

        return content.getContentString();
    }

}
