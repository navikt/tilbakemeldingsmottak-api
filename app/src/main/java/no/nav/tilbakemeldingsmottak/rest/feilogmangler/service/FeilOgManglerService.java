package no.nav.tilbakemeldingsmottak.rest.feilogmangler.service;

import jakarta.inject.Inject;
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService;
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class FeilOgManglerService {

    private static final Logger log = getLogger(FeilOgManglerService.class);

    private final AzureEmailService emailService;

    @Value("${email_nav_support_address}")
    private String emailToAddress;
    @Value("${email_from_address}")
    private String emailFromAddress;

    @Inject
    public FeilOgManglerService(AzureEmailService emailService) {
        this.emailService = emailService;
    }

    public void meldFeilOgMangler(MeldFeilOgManglerRequest request) {
        emailService.sendSimpleMessage(emailToAddress, "Feil/mangel på nav.no meldt via skjema på nav.no", createContent(request));
        log.info("Melding om feil og mangler videresendt til " + emailToAddress);
    }

    private String createContent(MeldFeilOgManglerRequest request) {
        HtmlContent content = new HtmlContent();

        if (request.getOnskerKontakt()) {
            content.addParagraph("Innsender ønsker å kontaktes på epost", request.getEpost());
        }
        content.addParagraph("Hva slags feil", request.getFeiltype().name());
        content.addParagraph("Melding", request.getMelding());

        return content.getContentString();
    }

}
