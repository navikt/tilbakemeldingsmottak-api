package no.nav.tilbakemeldingsmottak.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.api.SendRosRequest;
import no.nav.tilbakemeldingsmottak.service.epost.EmailServiceImpl;
import no.nav.tilbakemeldingsmottak.service.epost.HtmlContent;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class RosService {

    private EmailServiceImpl emailService;

    @Inject
    public RosService(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    public void sendRos(SendRosRequest request) throws MessagingException {
        sendEmail(request);
    }

    private void sendEmail(SendRosRequest request) throws MessagingException {
        MimeMessage message = emailService.getEmailSender().createMimeMessage();
        message.setHeader("Content-Encoding", "UTF-8");
        message.setContent(createContent(request), "text/html; charset=UTF-8");
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo("bjornar.hunshamar@trygdeetaten.no");
        helper.setFrom("srvtilbakemeldings@preprod.local");
        helper.setSubject("Ros mottatt");
        emailService.sendMail(message);
    }

    private String createContent(SendRosRequest request) {
        HtmlContent content = new HtmlContent();

        content.addParagraph("NAV-kontor", request.getNavKontor());
        content.addParagraph("Nærmere beskrivelse", request.getBeskrivelse());
        content.addParagraph("Innmelders navn", request.getNavn());

        return content.getContentString();
    }

}
