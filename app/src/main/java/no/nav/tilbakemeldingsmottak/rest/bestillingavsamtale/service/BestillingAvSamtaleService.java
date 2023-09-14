package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.service;

import jakarta.inject.Inject;
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService;
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest;
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class BestillingAvSamtaleService {

    private static final Logger log = getLogger(BestillingAvSamtaleService.class);
    private final AzureEmailService emailService;

    @Value("${email_samisk_kontakt_address}")
    private String emailToAddress;

    @Inject
    public BestillingAvSamtaleService(AzureEmailService emailService) {
        this.emailService = emailService;
    }

    public void bestillSamtale(BestillSamtaleRequest request) {
        emailService.sendSimpleMessage(emailToAddress, "Bestilling av samtale mottatt via skjema på nav.no", createContent(request));
        log.info("Bestilling av samtale videresendt til " + emailToAddress);
    }

    private String createContent(BestillSamtaleRequest request) {
        HtmlContent content = new HtmlContent();

        content.addParagraph("Fornavn", request.getFornavn());
        content.addParagraph("Etternavn", request.getEtternavn());
        content.addParagraph("Telefonnummer", request.getTelefonnummer());
        content.addParagraph("Tidsrom", request.getTidsrom().getValue());

        return content.getContentString();
    }

}
