package no.nav.tilbakemeldingsmottak.rest.ros.service;

import static no.nav.tilbakemeldingsmottak.rest.ros.domain.HvemRosesType.NAV_KONTOR;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.rest.common.epost.AbstractEmailService;
import no.nav.tilbakemeldingsmottak.rest.common.epost.HtmlContent;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class RosService {

    private AbstractEmailService emailService;

    @Value("${email_nav_support_address}")
    private String emailToAddress;
    @Value("${email_from_address}")
    private String emailFromAddress;

    @Inject
    public RosService(AbstractEmailService emailService) {
        this.emailService = emailService;
    }

    public void sendRos(SendRosRequest request) throws MessagingException {
        sendEmail(request);
        log.info("Ros sendt");
    }

    private void sendEmail(SendRosRequest request) throws MessagingException {
        MimeMessage message = emailService.getEmailSender().createMimeMessage();
        message.setHeader("Content-Encoding", "UTF-8");
        message.setContent(createContent(request), "text/html; charset=UTF-8");
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(emailToAddress);
        helper.setFrom(emailFromAddress);
        helper.setSubject("Ros mottatt");
        emailService.sendMail(message);
    }

    private String createContent(SendRosRequest request) {
        HtmlContent content = new HtmlContent();

        content.addParagraph("Navn", request.getNavn());
        content.addParagraph("Telefonnummer", request.getTelefonnummer());
        content.addParagraph("Hvem roses", request.getHvemRoses().text);
        if(NAV_KONTOR.equals(request.getHvemRoses())) {
            content.addParagraph("NAV-kontor", request.getNavKontor());
        }
        content.addParagraph("Melding", request.getMelding());

        return content.getContentString();
    }

}
