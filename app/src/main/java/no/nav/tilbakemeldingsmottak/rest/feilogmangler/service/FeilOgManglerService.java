package no.nav.tilbakemeldingsmottak.rest.feilogmangler.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.rest.common.epost.AbstractEmailService;
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.Feiltype;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class FeilOgManglerService {

    private AbstractEmailService emailService;

    @Value("${email_nav_support_address}")
    private String emailNavSupportAddress;
    @Value("${email_uu_address}")
    private String emailUuAddress;
    @Value("${email_from_address}")
    private String emailFromAddress;

    @Inject
    public FeilOgManglerService(AbstractEmailService emailService) {
        this.emailService = emailService;
    }

    public void meldFeilOgMangler(MeldFeilOgManglerRequest request) throws MessagingException {
        sendEmail(request);
    }

    private void sendEmail(MeldFeilOgManglerRequest request) throws MessagingException {
        String emailToAddress = Feiltype.UNIVERSELL_UTFORMING.equals(request.getFeiltype()) ? emailUuAddress : emailNavSupportAddress;

        MimeMessage message = emailService.getEmailSender().createMimeMessage();
        message.setHeader("Content-Encoding", "UTF-8");
        message.setContent(createContent(request), "text/html; charset=UTF-8");
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(emailToAddress);
        helper.setFrom(emailFromAddress);
        helper.setSubject("Feil/mangel meldt via skjema på nav.no");
        emailService.sendMail(message);

        log.info("Melding om feil/mangel videresendt til " + emailToAddress);
    }

    private String createContent(MeldFeilOgManglerRequest request) {
        HtmlContent content = new HtmlContent();

        if(request.getOnskerKontakt()) {
            content.addParagraph("Innsender ønsker å kontaktes på epost", request.getEpost());
        }
        content.addParagraph("Hva slags feil", request.getFeiltype().text);
        content.addParagraph("Melding", request.getMelding());

        return content.getContentString();
    }

}
