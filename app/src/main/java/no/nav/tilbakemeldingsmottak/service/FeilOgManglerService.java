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

    @Inject
    public FeilOgManglerService(AbstractEmailService emailService) {
        this.emailService = emailService;
    }

    public void meldFeilOgMangler(MeldFeilOgManglerRequest request) throws MessagingException {
        sendEmail(request);
    }

    private void sendEmail(MeldFeilOgManglerRequest request) throws MessagingException {
        MimeMessage message = emailService.getEmailSender().createMimeMessage();
        message.setHeader("Content-Encoding", "UTF-8");
        message.setContent(createContent(request), "text/html; charset=UTF-8");
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo("bjornar.hunshamar@trygdeetaten.no");
        helper.setFrom("srvtilbakemeldings@preprod.local");
        helper.setSubject("Feilmelding mottatt");
        emailService.sendMail(message);
    }

    private String createContent(MeldFeilOgManglerRequest request) {
        HtmlContent content = new HtmlContent();

        content.addParagraph("Navn", request.getNavn());
        content.addParagraph("Telefonnummer", request.getTelefonnummer());
        content.addParagraph("Hva slags feil", request.getFeiltype());
        content.addParagraph("Melding", request.getMelding());

        return content.getContentString();
    }

}
