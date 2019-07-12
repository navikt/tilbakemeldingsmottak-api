package no.nav.tilbakemeldingsmottak.service.pdf;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;

import java.io.ByteArrayOutputStream;

public class PdfCreator {

    private static Font regular = new Font(Font.FontFamily.HELVETICA, 12);
    private static Font bold = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);

    public static byte[] opprettPdf(OpprettServiceklageRequest request) throws DocumentException {
        Document document = new Document();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, stream);

        document.open();

        document.add(createParagraph("Navn til innmelder", request.getInnmelder().getNavn()));
        document.add(createParagraph("Telefonnummer til innmelder", request.getInnmelder().getTelefonnummer()));

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
                document.add(createParagraph("Innmelders rolle", request.getInnmelder().getRolle()));
                document.add(createParagraph("Navn til forulempet bedrift", request.getPaaVegneAvBedrift().getNavn()));
                document.add(createParagraph("Orgnr til forulempet bedrift", request.getPaaVegneAvBedrift().getOrganisasjonsnummer()));
                document.add(createParagraph("Adresse til forulempet bedrift", request.getPaaVegneAvBedrift().getPostadresse()));
                document.add(createParagraph("Telefonnummer til forulempet bedrift", request.getPaaVegneAvBedrift().getTelefonnummer()));
        }

        document.add(createParagraph("Klagetype", request.getKlagetype()));
        document.add(createParagraph("Klagetekst", request.getKlagetekst()));
        document.add(createParagraph("Ønsker å kontaktes", request.getOenskerAaKontaktes() ? "Ja" : "Nei"));

        document.close();

        return stream.toByteArray();
    }

    private static Paragraph createParagraph(String fieldname, String content) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(fieldname + ": ", bold));
        p.add(new Chunk(content, regular));
        return p;
    }
}
