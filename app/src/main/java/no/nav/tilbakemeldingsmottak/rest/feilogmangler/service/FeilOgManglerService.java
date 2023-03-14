package no.nav.tilbakemeldingsmottak.rest.feilogmangler.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService;
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent;
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
@Slf4j
public class FeilOgManglerService {

    private AzureEmailService emailService;

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

        if(request.getOnskerKontakt()) {
            content.addParagraph("Innsender ønsker å kontaktes på epost", request.getEpost());
        }
        content.addParagraph("Hva slags feil", request.getFeiltype().value);
        content.addParagraph("Melding", request.getMelding());

        return content.getContentString();
    }

}
