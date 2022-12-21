package no.nav.tilbakemeldingsmottak.rest.ros.service;

import static no.nav.tilbakemeldingsmottak.rest.ros.domain.HvemRosesType.NAV_KONTOR;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService;
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
@Slf4j
public class RosService {

    private AzureEmailService emailService;

    @Value("${email_nav_support_address}")
    private String emailToAddress;
    @Value("${email_from_address}")
    private String emailFromAddress;
    @Value("${spring.mail.host}")
    private String springMailHost;

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

        content.addParagraph("Hvem roses", request.getHvemRoses().text);
        if(NAV_KONTOR.equals(request.getHvemRoses())) {
            content.addParagraph("NAV-kontor", request.getNavKontor());
        }
        content.addParagraph("Melding", request.getMelding());

        return content.getContentString();
    }

}
