package no.nav.tilbakemeldingsmottak.rest.ros.service;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService;
import no.nav.tilbakemeldingsmottak.model.SendRosRequest;
import no.nav.tilbakemeldingsmottak.model.SendRosRequest.HvemRosesEnum;
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RosService {

    private final AzureEmailService emailService;

    @Value("${email_nav_support_address}")
    private String emailToAddress;
    @Value("${email_from_address}")
    private String emailFromAddress;

    @Inject
    public RosService(AzureEmailService emailService) {
        this.emailService = emailService;
    }

    public void sendRos(SendRosRequest request) {
        emailService.sendSimpleMessage(emailToAddress, "Ros til NAV sendt inn via skjema på nav.no", createContent(request));
        log.info("Ros til NAV videresendt til " + emailToAddress);
    }

    private String createContent(SendRosRequest request) {
        HtmlContent content = new HtmlContent();

        content.addParagraph("Hvem roses", request.getHvemRoses().toString());
        if (HvemRosesEnum.NAV_KONTOR.equals(request.getHvemRoses())) {
            content.addParagraph("NAV-kontor", request.getNavKontor());
        }
        content.addParagraph("Melding", request.getMelding());

        return content.getContentString();
    }

}
