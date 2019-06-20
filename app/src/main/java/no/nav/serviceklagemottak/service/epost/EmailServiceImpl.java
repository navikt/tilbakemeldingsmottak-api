package no.nav.serviceklagemottak.service.epost;

import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailServiceImpl {

    private final JavaMailSender emailSender;

    private static final String TO = "bjornar.hunshamar@trygdeetaten.no";
    private static final String FROM = "srvserviceklagemot@preprod.local";

    @Inject
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendMail(OpprettServiceklageRequest request, long id) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        message.setContent(createContent(request), "text/html");
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(TO);
        helper.setFrom(FROM);
        helper.setSubject("Serviceklage med id=" + id + " mottatt");
        emailSender.send(message);
    }

    private String createContent(OpprettServiceklageRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(createParagraph("Navn til innmelder", request.getInnmelder().getNavn()));
        stringBuilder.append(createParagraph("Telefonnummer til innmelder", request.getInnmelder().getTelefonnummer()));

        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON:
                stringBuilder.append(createParagraph("Personnummer til innmelder", request.getInnmelder().getPersonnummer()));
                break;
            case ANNEN_PERSON:
                stringBuilder.append(createParagraph("Innmelders rolle", request.getInnmelder().getRolle()));
                stringBuilder.append(createParagraph("Innmelder har fullmakt", request.getInnmelder().getHarFullmakt() ? "Ja" : "Nei"));
                stringBuilder.append(createParagraph("Navn til forulempet person", request.getPaaVegneAvPerson().getNavn()));
                stringBuilder.append(createParagraph("Personnummer til forulempet person", request.getPaaVegneAvPerson().getPersonnummer()));
                break;
            case BEDRIFT:
                stringBuilder.append(createParagraph("Innmelders rolle", request.getInnmelder().getRolle()));
                stringBuilder.append(createParagraph("Navn til forulempet bedrift", request.getPaaVegneAvBedrift().getNavn()));
                stringBuilder.append(createParagraph("Orgnr til forulempet bedrift", request.getPaaVegneAvBedrift().getOrganisasjonsnummer()));
                stringBuilder.append(createParagraph("Adresse til forulempet bedrift", request.getPaaVegneAvBedrift().getPostadresse()));
                stringBuilder.append(createParagraph("Telefonnummer til forulempet bedrift", request.getPaaVegneAvBedrift().getTelefonnummer()));
        }

        stringBuilder.append(createParagraph("Klagetype", request.getKlagetype()));
        stringBuilder.append(createParagraph("Klagetekst", request.getKlagetekst()));
        stringBuilder.append(createParagraph("Ønsker å kontaktes", request.getOenskerAaKontaktes() ? "Ja" : "Nei"));

        return stringBuilder.toString();
    }

    private String createParagraph(String fieldname, String content) {
        return String.format("<p><b>%s:</b> %s</p>", fieldname, content);
    }
}
