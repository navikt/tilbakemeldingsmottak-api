package no.nav.tilbakemeldingsmottak.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.service.epost.AbstractEmailService;
import no.nav.tilbakemeldingsmottak.service.epost.HtmlContent;
import no.nav.tilbakemeldingsmottak.service.mappers.OpprettServiceklageRequestMapper;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class ServiceklageService {

    private ServiceklageRepository serviceklageRepository;
    private AbstractEmailService emailService;
    private final OpprettServiceklageRequestMapper opprettServiceklageRequestMapper;

    @Inject
    public ServiceklageService(ServiceklageRepository serviceklageRepository, AbstractEmailService emailService, OpprettServiceklageRequestMapper opprettServiceklageRequestMapper) {
        this.serviceklageRepository = serviceklageRepository;
        this.emailService = emailService;
        this.opprettServiceklageRequestMapper = opprettServiceklageRequestMapper;
    }

    public long opprettServiceklage(OpprettServiceklageRequest request) throws MessagingException {
        Serviceklage serviceklage = opprettServiceklageRequestMapper.map(request);

        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} persistert", serviceklage.getServiceklageId());
        sendEmail(request, serviceklage.getServiceklageId());

        return serviceklage.getServiceklageId();
    }

    private void sendEmail(OpprettServiceklageRequest request, Long id) throws MessagingException {
        MimeMessage message = emailService.getEmailSender().createMimeMessage();
        message.setHeader("Content-Encoding", "UTF-8");
        message.setContent(createContent(request), "text/html; charset=UTF-8");
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo("bjornar.hunshamar@trygdeetaten.no");
        helper.setFrom("srvtilbakemeldings@preprod.local");
        helper.setSubject("Serviceklage med id=" + id + " mottatt");
        emailService.sendMail(message);
    }

    private String createContent(OpprettServiceklageRequest request) {
        HtmlContent content = new HtmlContent();

        content.addParagraph("Navn til innmelder", request.getInnmelder().getNavn());
        content.addParagraph("Telefonnummer til innmelder", request.getInnmelder().getTelefonnummer());

        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON:
                content.addParagraph("Personnummer til innmelder", request.getInnmelder().getPersonnummer());
                break;
            case ANNEN_PERSON:
                content.addParagraph("Innmelders rolle", request.getInnmelder().getRolle());
                content.addParagraph("Innmelder har fullmakt", request.getInnmelder().getHarFullmakt() ? "Ja" : "Nei");
                content.addParagraph("Navn til forulempet person", request.getPaaVegneAvPerson().getNavn());
                content.addParagraph("Personnummer til forulempet person", request.getPaaVegneAvPerson().getPersonnummer());
                break;
            case BEDRIFT:
                content.addParagraph("Innmelders rolle", request.getInnmelder().getRolle());
                content.addParagraph("Navn til forulempet bedrift", request.getPaaVegneAvBedrift().getNavn());
                content.addParagraph("Orgnr til forulempet bedrift", request.getPaaVegneAvBedrift().getOrganisasjonsnummer());
                content.addParagraph("Adresse til forulempet bedrift", request.getPaaVegneAvBedrift().getPostadresse());
                content.addParagraph("Telefonnummer til forulempet bedrift", request.getPaaVegneAvBedrift().getTelefonnummer());
        }

        content.addParagraph("Klagetype", request.getKlagetype());
        content.addParagraph("Klagetekst", request.getKlagetekst());
        content.addParagraph("Ønsker å kontaktes", request.getOenskerAaKontaktes() ? "Ja" : "Nei");

        return content.getContentString();
    }
}
