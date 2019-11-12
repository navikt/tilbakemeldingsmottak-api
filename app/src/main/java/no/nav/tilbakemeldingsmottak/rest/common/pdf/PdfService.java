package no.nav.tilbakemeldingsmottak.rest.common.pdf;

import static no.nav.tilbakemeldingsmottak.config.Constants.AZURE_ISSUER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class PdfService {

    private Font regular = new Font(Font.FontFamily.HELVETICA, 14);
    private Font bold = new  Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private Font boldUnderline = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD|Font.UNDERLINE);

    private final OidcUtils oidcUtils;

    public byte[] opprettPdf(OpprettServiceklageRequest request) throws DocumentException {
        Document document = new Document();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, stream);

        document.open();

        if (!oidcUtils.getSubjectForIssuer(AZURE_ISSUER).isPresent()) {
            document.add(createUinnloggetHeader());
            document.add(Chunk.NEWLINE);
        }

        document.add(createParagraph("Kanal", KANAL_SERVICEKLAGESKJEMA_ANSWER));

        if (!isBlank(request.getInnmelder().getNavn())) {
            document.add(createParagraph("Navn til innmelder", request.getInnmelder().getNavn()));
        }

        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON:
                document.add(createParagraph("Personnummer til innmelder", request.getInnmelder().getPersonnummer()));
                break;
            case ANNEN_PERSON:
                document.add(createParagraph("Innmelders rolle", request.getInnmelder().getRolle()));
                document.add(createParagraph("Innmelder har fullmakt", request.getInnmelder().getHarFullmakt() ? "Ja" : "Nei"));
                document.add(createParagraph("Navn til forulempet person", request.getPaaVegneAvPerson().getNavn()));
                document.add(createParagraph("Personnummer til forulempet person", request.getPaaVegneAvPerson().getPersonnummer()));
                break;
            case BEDRIFT:
                if (!isBlank(request.getInnmelder().getRolle())) {
                    document.add(createParagraph("Innmelders rolle", request.getInnmelder().getRolle()));
                }
                document.add(createParagraph("Navn til forulempet bedrift", request.getPaaVegneAvBedrift().getNavn()));
                document.add(createParagraph("Orgnr til forulempet bedrift", request.getPaaVegneAvBedrift().getOrganisasjonsnummer()));
        }


        if (!isBlank(request.getEnhetsnummerPaaklaget())) {
            document.add(createParagraph("Påklaget enhet", request.getEnhetsnummerPaaklaget()));
        }
        document.add(createParagraph("Klagetype", StringUtils.join(request.getKlagetyper().stream().map(k -> k.text).collect(Collectors.toList()), ", ")));
        if (request.getKlagetyper().contains(Klagetype.LOKALT_NAV_KONTOR)) {
            document.add(createParagraph("Gjelder økonomisk sosialhjelp/sosiale tjenester", request.getGjelderSosialhjelp().text));
        }
        document.add(createParagraph("Klagetekst", request.getKlagetekst()));
        if (request.getOenskerAaKontaktes() != null) {
            document.add(createParagraph("Ønsker å kontaktes", request.getOenskerAaKontaktes() ? "Ja" : "Nei"));
        }
        if (!isBlank(request.getInnmelder().getTelefonnummer())) {
            document.add(createParagraph("Telefonnummer til innmelder", request.getInnmelder().getTelefonnummer()));
        }

        document.close();

        return stream.toByteArray();
    }

    private Paragraph createParagraph(String fieldname, String content) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(fieldname + ": ", bold));
        p.add(new Chunk(content, regular));
        return p;
    }

    private Paragraph createUinnloggetHeader() {
        Paragraph p = new Paragraph();
        p.add(new Chunk("OBS! Klagen er sendt inn uinnlogget", boldUnderline));
        return p;
    }
}